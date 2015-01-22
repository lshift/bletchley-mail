package net.lshift.bletchley.mail;

import net.lshift.spki.InvalidInputException;
import net.lshift.spki.convert.ConvertUtils;
import net.lshift.spki.suiteb.SequenceItem;

import org.junit.Test;

public class SenderTest extends TestServices {
    @Test
    public void testWrap() {
        sender.wrap(sampleMessage());
    }

    @Test
    public void testConversion() throws InvalidInputException {
        ConvertUtils.fromBytes(
                Actions.READ_INFO, 
                SequenceItem.class, 
                ConvertUtils.toBytes(sender.wrap(sampleMessage())));
    }
}
