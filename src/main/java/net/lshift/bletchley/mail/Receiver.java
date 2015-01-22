package net.lshift.bletchley.mail;

import java.util.Map;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.suiteb.CryptographyException;
import net.lshift.spki.suiteb.InferenceEngine;
import net.lshift.spki.suiteb.PrivateEncryptionKey;
import net.lshift.spki.suiteb.PublicSigningKey;
import net.lshift.spki.suiteb.SequenceItem;

public class Receiver {
    private final PrivateEncryptionKey encryptionKey;
    private final Map<Address,PublicSigningKey> senders;

    public Receiver(PrivateEncryptionKey encryptionKey,
            Map<Address, PublicSigningKey> senders) {
        super();
        this.encryptionKey = encryptionKey;
        this.senders = senders;
    }

    public Message unwrap(SequenceItem message) throws InvalidInputException {
        InferenceEngine engine = new InferenceEngine(Actions.READ_INFO);
        engine.processTrusted(encryptionKey);
        /* find out who the message claims to be sent by, then re-process
         * the message, trusting only the public signing key of the sender,
         * if we have a key which we trust for that sender.
         */
        // FIXME: NullPointerException if we don't know the sender
        engine.processTrusted(senders.get(untrustedSender(message)));
        engine.process(message);
        return engine.getSoleAction(Message.class);
    }

    private Address untrustedSender(SequenceItem message)
            throws InvalidInputException, CryptographyException {
        InferenceEngine engine = new InferenceEngine(Actions.READ_INFO);
        engine.processTrusted(encryptionKey);
        /*
             Here, we trust the whole message in order to use the inference
             engine to decrypt the message, so we can extract the sender.
         */
        engine.processTrusted(message);
        return engine.getSoleAction(Message.class).sender;
    }
}
