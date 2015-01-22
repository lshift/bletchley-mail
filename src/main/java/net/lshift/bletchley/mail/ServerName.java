package net.lshift.bletchley.mail;

import net.lshift.spki.convert.SexpBacked;
import net.lshift.spki.convert.Convert;

@Convert.ByPosition(name="dns-name", fields="name")
public class ServerName extends SexpBacked {
    public final String name;

    public ServerName(String name) {
        super();
        this.name = name;
    }
}
