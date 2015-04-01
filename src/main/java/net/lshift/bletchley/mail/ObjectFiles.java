package net.lshift.bletchley.mail;

import java.io.File;

import net.lshift.spki.convert.Writeable;
import net.lshift.spki.suiteb.DigestSha384;

import org.bouncycastle.util.encoders.Hex;

public class ObjectFiles {

    public static File idFile(File base, Writeable message) {
        return new File(base, id(message));
    }

    public static String id(Writeable message) {
        return Hex.toHexString(DigestSha384.digest(message).getBytes(), 0, 16);
    }

}
