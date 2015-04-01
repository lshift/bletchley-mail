package net.lshift.bletchley.mail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.suiteb.SequenceItem;

public interface Spool {
    /**
     * Enqueue the tasks found in message
     * @param message
     * @throws IOException 
     * @throws InterruptedException 
     * @throws InvalidInputException 
     */
    public void spool(SequenceItem message) 
            throws IOException, InterruptedException, InvalidInputException;

    public void acknowledge(SequenceItem message)
            throws IOException, InterruptedException;

    public void retry(SequenceItem message, long delay, TimeUnit unit)
            throws IOException, InterruptedException;
}
