package net.lshift.bletchley.mail;

import net.lshift.spki.convert.ConverterCatalog;

public class Actions {
    /**
     * ReadInfo for all the actions used by the system.
     */
    public static final ConverterCatalog READ_INFO = ConverterCatalog.BASE.extend(
            Deliver.class, Message.class, Relay.class,
            Task.class, Completed.class);
}
