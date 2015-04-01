package net.lshift.bletchley.mail;

import java.io.IOException;

import net.lshift.spki.InvalidInputException;

public interface Consumer<T> {
    public void accept(T t)
    throws IOException, InvalidInputException, InterruptedException;
}
