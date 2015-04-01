package net.lshift.bletchley.mail;

import static net.lshift.bletchley.mail.Actions.READ_INFO;
import static net.lshift.bletchley.mail.ObjectFiles.idFile;
import static net.lshift.spki.convert.ConvertUtils.read;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.suiteb.Action;
import net.lshift.spki.suiteb.ActionType;
import net.lshift.spki.suiteb.InferenceEngine;
import net.lshift.spki.suiteb.Sequence;
import net.lshift.spki.suiteb.SequenceItem;

/**
 * Log activity using Bletchley.
 * This is incomplete. See the FIXMEs.
 */
public class FileLog implements Log {
    public final File log;

    public FileLog(File log) {
        this.log = log;
    }

    @Override
    public void log(Task task, ActionType action) throws IOException {
        List<ActionType> actions = Lists.newArrayList();
        Iterables.addAll(actions, logged(task));
        actions.add(action);
        // FIXME: write to a temporary file then move
        // FIXME: used a chained structure and sign the actions, so they
        // can't be forged.
        ConvertUtils.write(
                new Sequence(Lists.transform(actions, (a) -> new Action(a))),
                idFile(log, task));
    }

    @Override
    public Iterable<ActionType> logged(Task task) throws IOException {
        File logFile = idFile(this.log, task);
        try {
            if(logFile.exists()) {
                SequenceItem log = read(READ_INFO, SequenceItem.class, logFile);
                InferenceEngine e = new InferenceEngine(READ_INFO);
                // FIXME: don't trust the logs
                e.processTrusted(log);
                return e.getActions();
            } else {
                return ImmutableList.of();
            }
        } catch (InvalidInputException e) {
            throw new RuntimeException("Invalid log in " + logFile, e);
        }
    }

}
