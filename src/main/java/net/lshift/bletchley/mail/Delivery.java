package net.lshift.bletchley.mail;

import java.util.concurrent.TimeUnit;

import net.lshift.spki.suiteb.SequenceItem;

public interface Delivery {
    public SequenceItem body();
    public void acknowledge();
    public void retry(long delay, TimeUnit minutes);
}
