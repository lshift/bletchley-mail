package net.lshift.bletchley.mail;

import net.lshift.spki.suiteb.PublicEncryptionKey;

public class RemoteReceiver {
    public final Address address;
    public final PublicEncryptionKey encryptionKey;
    public RemoteReceiver(Address address, PublicEncryptionKey encryptionKey) {
        super();
        this.address = address;
        this.encryptionKey = encryptionKey;
    }
}
