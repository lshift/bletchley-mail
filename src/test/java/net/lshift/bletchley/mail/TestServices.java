package net.lshift.bletchley.mail;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;

import net.lshift.bletchley.mail.Address;
import net.lshift.bletchley.mail.Message;
import net.lshift.bletchley.mail.Receiver;
import net.lshift.bletchley.mail.RemoteServer;
import net.lshift.bletchley.mail.Sender;
import net.lshift.bletchley.mail.ServerName;
import net.lshift.spki.suiteb.PrivateEncryptionKey;
import net.lshift.spki.suiteb.PrivateSigningKey;
import net.lshift.spki.suiteb.PublicEncryptionKey;
import net.lshift.spki.suiteb.PublicSigningKey;
import net.lshift.spki.suiteb.simplemessage.SimpleMessage;

import org.junit.Before;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

public class TestServices {

    private static final ServerName TEST_SENDER_SERVER = new ServerName("sender.test.com");
    protected static final ServerName TEST_RECEIVER_SERVER = new ServerName("receiver.test.com");
    private static final Address TEST_SENDER = new Address(TEST_SENDER_SERVER, "sender");
    protected static final Address TEST_RECEIVER = new Address(TEST_RECEIVER_SERVER, "receiver");

    protected static Message sampleMessage() {
        return new Message(
                UUID.randomUUID(), 
                TEST_SENDER, 
                ImmutableList.of(TEST_RECEIVER), 
                ImmutableList.<Address>of(),
                new SimpleMessage("text/plain", "Hello".getBytes(Charset.defaultCharset())));
    }

    protected PrivateSigningKey senderSigningKey;
    protected Sender sender;
    private PrivateEncryptionKey senderServerEncryptionKey;
    private RemoteServer senderRemoteServer;
    private PrivateEncryptionKey receiverServerEncryptionKey;
    private RemoteServer receiverRemoteServer;
    private PrivateEncryptionKey receiverEncryptionKey;
    protected Receiver receiver;
    protected Server senderServer;
    protected Server receiverServer;

    public TestServices() {
        super();
    }

    @Before
    public void setup() {
        senderServerEncryptionKey = PrivateEncryptionKey.generate();
        senderRemoteServer = new RemoteServer(
                TEST_SENDER_SERVER, 
                senderServerEncryptionKey.getPublicKey(), null);
        senderServer = new Server(senderServerEncryptionKey);
        receiverServerEncryptionKey = PrivateEncryptionKey.generate();
        receiverRemoteServer = new RemoteServer(
                TEST_RECEIVER_SERVER,
                receiverServerEncryptionKey.getPublicKey(), null);
        receiverServer = new Server(receiverServerEncryptionKey);
        @SuppressWarnings("unchecked")
        Map<ServerName,RemoteServer> servers = Mockito.mock(Map.class);
        Mockito.when(servers.get(TEST_RECEIVER_SERVER)).thenReturn(receiverRemoteServer);
        receiverEncryptionKey = PrivateEncryptionKey.generate();
        @SuppressWarnings("unchecked")
        Map<Address,PublicEncryptionKey> receivers = Mockito.mock(Map.class);
        Mockito.when(receivers.get(TEST_RECEIVER)).thenReturn(receiverEncryptionKey.getPublicKey());
        senderSigningKey = PrivateSigningKey.generate();
        sender = new Sender(senderSigningKey, receivers, servers, senderRemoteServer);

        @SuppressWarnings("unchecked")
        Map<Address, PublicSigningKey> senders = Mockito.mock(Map.class);
        Mockito.when(senders.get(TEST_SENDER)).thenReturn(senderSigningKey.getPublicKey());
        receiver = new Receiver(receiverEncryptionKey, senders);
    }

}