package net.lshift.bletchley.mail;

import net.lshift.spki.convert.ReadInfo;

public class Actions {
    /**
     * ReadInfo for all the actions used by the system.
     */
    public static final ReadInfo READ_INFO = ReadInfo.BASE.extend(Deliver.class, Message.class, Relay.class);
}
