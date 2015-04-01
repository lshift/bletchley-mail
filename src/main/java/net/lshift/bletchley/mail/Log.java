package net.lshift.bletchley.mail;

import java.io.IOException;

import net.lshift.spki.suiteb.ActionType;

public interface Log {
    public void log(Task task, ActionType action)
        throws IOException;
    public Iterable<ActionType> logged(Task task)
        throws IOException;
}
