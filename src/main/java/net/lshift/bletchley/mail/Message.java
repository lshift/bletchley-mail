package net.lshift.bletchley.mail;

import java.util.List;
import java.util.UUID;

import net.lshift.spki.convert.Convert;
import net.lshift.spki.suiteb.ActionType;
import net.lshift.spki.suiteb.simplemessage.SimpleMessage;

@Convert.ByName("message")
public class Message extends SexpEquality implements ActionType {
    public final UUID id;
    public final Address sender;
    public final List<Address> to;
    public final List<Address> cc;
    public final SimpleMessage body;

    public Message(
            UUID id,
            Address sender,
            List<Address> to,
            List<Address> cc,
            SimpleMessage body) {
        super();
        this.id = id;
        this.sender = sender;
        this.to = to;
        this.cc = cc;
        this.body = body;
    }
}
