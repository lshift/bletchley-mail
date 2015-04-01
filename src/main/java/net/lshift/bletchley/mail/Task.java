package net.lshift.bletchley.mail;

import net.lshift.spki.suiteb.ActionType;
import net.lshift.spki.suiteb.SequenceItem;
import net.lshift.spki.convert.Convert;

/**
 * A task for the server.
 * This only exists so I can create a task identifier, which I use to achieve
 * idempotence: I just record the id's of the tasks I've finished in order
 * to ignore them if I see them again.
 */
@Convert.ByPosition(name="task", fields={"action", "message"})
public class Task extends SexpEquality implements ActionType {
    public final ActionType action;
    public final SequenceItem message;

    public Task(ActionType action, SequenceItem message) {
        super();
        this.action = action;
        this.message = message;
    }
}
