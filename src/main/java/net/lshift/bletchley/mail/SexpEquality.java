package net.lshift.bletchley.mail;

import java.util.Arrays;

import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.convert.SexpBacked;
import net.lshift.spki.convert.Writeable;

public class SexpEquality extends SexpBacked {
    @Override
    public final boolean equals(Object o) {
        if(this.getClass() == o.getClass()) {
            return Arrays.equals(ConvertUtils.toBytes(this), ConvertUtils.toBytes((Writeable)o));
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(ConvertUtils.toBytes(this));
    }
}
