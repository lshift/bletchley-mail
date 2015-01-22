package net.lshift.bletchley.mail;

import static com.google.common.base.Functions.forMap;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static net.lshift.spki.suiteb.SequenceUtils.sequence;
import static net.lshift.spki.suiteb.Signed.signed;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.lshift.spki.suiteb.Action;
import net.lshift.spki.suiteb.AesKey;
import net.lshift.spki.suiteb.EncryptionCache;
import net.lshift.spki.suiteb.PrivateSigningKey;
import net.lshift.spki.suiteb.PublicEncryptionKey;
import net.lshift.spki.suiteb.Sequence;
import net.lshift.spki.suiteb.SequenceItem;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Put the message in the addressed envelope.
 * There are two actors involved in the delivery: the senders server and
 * the recipients server. The address consists of a list of recipient servers,
 * ({@link Relay}) which is used by the senders server and a list
 * of destination addresses ({@link Delivery}) which are used by the
 * recipient servers to delivery the message to the recipients inbox.
 * @see #addressedEnvelope(Message)
 */
public class Sender {
    private final PrivateSigningKey signingKey;
    private final Map<Address, PublicEncryptionKey> recipients;
    private final Map<ServerName, RemoteServer> servers;
    private final RemoteServer relay;

    /*
         Message consumers only require that they are able to decrypt
         the actions they need to see, and that the actions are signed by
         a key they trust. This mail system tries to implement the
         principal of least privilege. How that is implemented is up to this
         class: none of the consumers care beyond the above.

         This system supports multiple recipients, mostly so I can demonstrate
         how multiple recipients can be implemented efficiently, but this exposes
         some information: the number of recipients and the number of distinct
         recipient servers.

         FIXME: I could demonstrate this another way, by supporting multiple
         keys for the same server. HA is a good reason to support multiple keys:
         we don't want to be forced to replicate private keys to achieve HA. In
         fact we don't want recipients to have to replicate private keys, so
         they will probably also have multiple encryption keys.
     */

    /**
     * Constructor.
     * @see #addressedEnvelope(Message) for descriptions of how the parameters
     * are used.
     */
    public Sender(PrivateSigningKey signingKey,
            Map<Address, PublicEncryptionKey> recipients,
            Map<ServerName, RemoteServer> servers,
            RemoteServer relay) {
        super();
        this.signingKey = signingKey;
        this.recipients = recipients;
        this.servers = servers;
        this.relay = relay;
    }

    /**
     * Put the message in an addressed envelope.
     * This method encrypts {@link Relay} actions using the public encryption
     * key of the senders server, and {@link Delivery} actions are grouped by
     * recipient server, each group being encrypted using the corresponding
     * recipient server public encryption key, from {@link #servers}. 
     * The {@link Message} part is signed with {@link #signingKey}. It is then 
     * encrypted for each recipient (defined in {@link Message#to} and 
     * {@link Message#cc}) using the corresponding keys from {@link #recipients}
     * @param message
     * @return a sequence for the addressed envelope.
     */
    public Sequence addressedEnvelope(Message message) {
        // Organise addresses by destination server
        Multimap<ServerName,Address> destinations = HashMultimap.create();
        for(Address recipient: Iterables.concat(message.to, message.cc)) {
            destinations.put(recipient.server, recipient);
        }

        return sequence(
                encryptedRelays(destinations),
                encryptedDeliveries(destinations),
                signedEncryptedMessage(message));
    }

    private SequenceItem signedEncryptedMessage(Message message) {
        /* 
             This code encrypts the message in a way that allows multiple
             recipients to decrypt it. It starts by creating a symmetric 
             key that I will use to encrypt the message. This could be encrypted
             using each recipients private key, but Bletchley provides a 
             convenient mechanism for securely establishing a shared key which 
             only works between a pair of sender and recipient. This 
             mechanism is used to share a single key with each recipient in turn.

             Having told all the recipients the message key, the message 
             encrypted with the key is added.
         */

        final AesKey messageKey = AesKey.generateAESKey();
        Iterable<PublicEncryptionKey> recipientKeys = 
                transform(concat(message.to, message.cc), forMap(recipients));
        // FIXME: recipientKeys might contain nulls
        return sequence(
                shareKey(messageKey, recipientKeys),
                messageKey.encrypt(
                    sequence(
                        signingKey.getPublicKey(), 
                        signed(signingKey, new Action(message)))));

    }

    private SequenceItem shareKey(AesKey key, Iterable<PublicEncryptionKey> recipients) {
        final EncryptionCache ephemeral = EncryptionCache.ephemeralKey();
        List<SequenceItem> sequence = Lists.newArrayList();
        sequence.add(ephemeral.getPublicKey());
        for(PublicEncryptionKey recipient: recipients) {
            sequence.add(ephemeral.encrypt(recipient, key));
        }
        return new Sequence(sequence);
    }

    private Sequence encryptedRelays(Multimap<ServerName, Address> destinations) {
        List<SequenceItem> relays = Lists.newArrayList();
        for(ServerName server: destinations.keySet()) {
            relays.add(new Action(new Relay(server)));
        }

        final EncryptionCache ephemeral = EncryptionCache.ephemeralKey();
        return sequence(
            ephemeral.getPublicKey(),
            ephemeral.encrypt(
                    relay.encryptionKey, 
                    new Sequence(relays)));
    }

    private Sequence encryptedDeliveries(Multimap<ServerName, Address> destinations) {
        List<SequenceItem> deliveries = Lists.newArrayList();
        for(Map.Entry<ServerName, Collection<Address>> dest: destinations.asMap().entrySet()) {
            deliveries.add(encryptedDeliveries(dest.getKey(), dest.getValue()));
        }
        return new Sequence(deliveries);
    }

    private Sequence encryptedDeliveries(ServerName server, Collection<Address> addresses) {
        final EncryptionCache ephemeral = EncryptionCache.ephemeralKey();
        List<SequenceItem> deliveries = Lists.newArrayList();
        for(Address address: addresses) {
            deliveries.add(new Action(new Deliver(address)));
        }

        return sequence(
            ephemeral.getPublicKey(),
            // FIXME: NullPointerException if we don't know the recipient's server
            ephemeral.encrypt(
                    servers.get(server).encryptionKey,
                    new Sequence(deliveries)));
    }
}
