package net.lshift.bletchley.mail;

import net.lshift.spki.convert.Convert;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.convert.SexpBacked;
import net.lshift.spki.suiteb.PublicEncryptionKey;
import net.lshift.spki.suiteb.PublicSigningKey;

@Convert.ByPosition(name="server", fields="name")
public class RemoteServer extends SexpBacked {
    public final ServerName name;
    public final PublicEncryptionKey encryptionKey;
    public final PublicSigningKey signingKey;

    public RemoteServer(ServerName name, PublicEncryptionKey encryptionKey,
            PublicSigningKey signingKey) {
        super();
        this.name = name;
        this.encryptionKey = encryptionKey;
        this.signingKey = signingKey;
    }

    @Override
    public String toString() {
        return ConvertUtils.prettyPrint(this);
    }


}
