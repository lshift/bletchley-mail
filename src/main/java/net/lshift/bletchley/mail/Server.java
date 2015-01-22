package net.lshift.bletchley.mail;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.suiteb.ActionType;
import net.lshift.spki.suiteb.InferenceEngine;
import net.lshift.spki.suiteb.PrivateEncryptionKey;
import net.lshift.spki.suiteb.SequenceItem;


public class Server {
    public final PrivateEncryptionKey encryptionKey;

    public Server(PrivateEncryptionKey encryptionKey) {
        super();
        this.encryptionKey = encryptionKey;
    }

    public <T extends ActionType> List<T> actions(Class<T> type, SequenceItem message) 
    throws InvalidInputException {
        InferenceEngine engine = new InferenceEngine(Actions.READ_INFO);
        engine.processTrusted(encryptionKey);
        engine.processTrusted(message);
        return Lists.newArrayList(Iterables.filter(engine.getActions(), type));
    }
}
