package net.lshift.bletchley.mail;

import static com.google.common.collect.Iterables.contains;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.suiteb.ActionType;
import net.lshift.spki.suiteb.InferenceEngine;
import net.lshift.spki.suiteb.PrivateEncryptionKey;
import net.lshift.spki.suiteb.SequenceItem;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Functions to extract server actions from a message.
 * There's also the beginnings of a small raw socket
 * based server here.
 */
public class Server implements Consumer<Delivery> {
    public static final int SERVER_PORT = 5433;

    public final PrivateEncryptionKey encryptionKey;
    private final Log log;

    /**
     * Constructor
     * @param encryptionKey the servers private encryption key
     */
    public Server(PrivateEncryptionKey encryptionKey, Log log) {
        super();
        this.encryptionKey = encryptionKey;
        this.log = log;
    }

    public <T extends ActionType> List<T> actions(Class<T> type, SequenceItem message)
    throws InvalidInputException {
        InferenceEngine engine = new InferenceEngine(Actions.READ_INFO);
        engine.processTrusted(encryptionKey);
        engine.processTrusted(message);
        return Lists.newArrayList(Iterables.filter(engine.getActions(), type));
    }

    public List<Deliver> deliveries(SequenceItem message) 
    throws InvalidInputException {
        return actions(Deliver.class, message);
    }

    public List<Relay> relays(SequenceItem message) 
    throws InvalidInputException {
        return actions(Relay.class, message);
    }

    @Override
    public void accept(Delivery delivery) 
    throws IOException, InvalidInputException, InterruptedException {
        SequenceItem message = delivery.body();
        if(consume(message, 
                deliveries(message), 
                (deliver) -> deliver(deliver.receiver, message))
           && consume(message,
                 relays(message),
                 (relay) -> relay(relay.server, message))) {
            delivery.acknowledge();
        } else {
            delivery.retry(10, TimeUnit.MINUTES);
        }
    }

    private <T extends ActionType> boolean consume(
            SequenceItem message, 
            Iterable<T> actions,
            Consumer<T> consumer) 
    throws InvalidInputException, IOException, InterruptedException {
        boolean succeeded = true;
        for(T action: actions) {
            Task task = new Task(action, message);
            if(!contains(log.logged(task), Completed.INSTANCE)) {
                try {
                    consumer.accept(action);
                    log.log(task, Completed.INSTANCE);
                } catch(IOException e) {
                    succeeded = false;
                }
            }
        }

        return succeeded;
    }

    private boolean relay(ServerName server, SequenceItem message) {
        // TODO Auto-generated method stub
        return true;
    }

    private boolean deliver(Address receiver, SequenceItem message) {
        // TODO Auto-generated method stub
        return true;
    }

    public static void main(String [] args) {
        Log log = new FileLog(new File("./log").getAbsoluteFile());
        Server server = new Server(PrivateEncryptionKey.generate(), log);
        final Spool spool = new FileSpool(
                new File("./spool").getAbsoluteFile(), server);

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);) {
            while(true) {
                try(Socket clientSocket = serverSocket.accept();
                        InputStream in = clientSocket.getInputStream();
                        OutputStream out = clientSocket.getOutputStream(); ) {
                    spool.spool(ConvertUtils.read(Actions.READ_INFO, SequenceItem.class, in));
                } catch (IOException e) {
                    report(e, false);
                } catch (InvalidInputException e) {
                    report(e, false);
                }
            }
        } catch (IOException e) {
            report(e, true);
        } catch(InterruptedException e) {
            report(e, true);
        }
    }

    private static void report(Exception e, boolean b) {
        e.printStackTrace();
    }


}
