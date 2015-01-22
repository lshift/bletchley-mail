package net.lshift.bletchley.mail;

import net.lshift.spki.convert.Convert;

/**
 * Email address of the recipient, 
 */
@Convert.ByPosition(name="address", fields = {"server", "user"})
public class Address extends SexpEquality {
    public final ServerName server;
    public final String user;
    public Address(ServerName server, String user) {
        super();
        this.server = server;
        this.user = user;
    }
}
