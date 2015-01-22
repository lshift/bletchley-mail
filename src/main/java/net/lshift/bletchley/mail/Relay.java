package net.lshift.bletchley.mail;

import net.lshift.spki.convert.Convert;
import net.lshift.spki.suiteb.ActionType;

/**
 * This tells the server 'please relay this message to this other server'.
 */
@Convert.ByPosition(name="relay", fields="server")
public class Relay extends SexpEquality implements ActionType {
    public final ServerName server;

    public Relay(ServerName server) {
        this.server = server;
    }
}
