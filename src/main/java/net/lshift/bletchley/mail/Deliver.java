package net.lshift.bletchley.mail;

import net.lshift.spki.convert.Convert;
import net.lshift.spki.suiteb.ActionType;

/**
 * Action for the server: please deliver this message
 * to these mail boxes.
 */
@Convert.ByPosition(fields = { "receiver" }, name = "deliver")
public class Deliver extends SexpEquality implements ActionType {
    public final Address receiver;

    public Deliver(Address receiver) {
        this.receiver = receiver;
    }
}
