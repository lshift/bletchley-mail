package net.lshift.bletchley.mail;

import net.lshift.spki.convert.Convert;
import net.lshift.spki.convert.SexpBacked;
import net.lshift.spki.suiteb.ActionType;

@Convert.ByPosition(name="completed", fields={})
public class Completed extends SexpBacked implements ActionType {

    public static final Completed INSTANCE = new Completed();

    public boolean equals(Object other) {
        return this.getClass().equals(other.getClass());
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}
