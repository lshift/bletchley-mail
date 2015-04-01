package net.lshift.bletchley.mail;

import static net.lshift.bletchley.mail.ObjectFiles.idFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.suiteb.SequenceItem;

/**
 * Save messages on disk for later processing.
 * This just shows the pattern of saving messages directly
 * without processing.
 */
public class FileSpool implements Spool {

    public final File spool;
    public final Consumer<Delivery> consumer;

    private final ScheduledExecutorService executor;

    public FileSpool(
            File spool,
            Consumer<Delivery> consumer) {
        this.spool = spool;
        this.consumer = consumer;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void resume()
            throws IOException,
                InvalidInputException,
                InterruptedException {
        this.spool.mkdirs();
        for(File file: this.spool.listFiles()) {
            SequenceItem x = ConvertUtils.read(Actions.READ_INFO, SequenceItem.class, file);
            executor.submit(callable(x));
        }
    }

    @Override
    public void spool(SequenceItem message) 
            throws IOException, InterruptedException, InvalidInputException {
        ConvertUtils.write(message, idFile(spool, message));
        executor.submit(callable(message));
    }

    @Override
    public void acknowledge(SequenceItem message) {
        idFile(spool, message).delete();
    }

    @Override
    public void retry(SequenceItem message, long delay, TimeUnit unit) {
        executor.schedule(callable(message), delay, unit);
    }

    private Callable<Object> callable(final SequenceItem message) {
        return () -> { 
            consumer.accept(new Delivery() {
                public SequenceItem body() {
                    return message;
                }

                @Override
                public void acknowledge() {
                    FileSpool.this.acknowledge(message);
                }

                @Override
                public void retry(long delay, TimeUnit minutes) {
                    FileSpool.this.retry(message, delay, minutes);
                }
            });
            return null;
        };
    }


}
