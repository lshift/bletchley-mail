package net.lshift.bletchley.mail;

import static net.lshift.spki.suiteb.SequenceUtils.sequence;
import static net.lshift.spki.suiteb.Signed.signed;
import static org.junit.Assert.assertEquals;
import net.lshift.spki.InvalidInputException;
import net.lshift.spki.suiteb.Action;

import org.junit.Test;

public class ReceiverTest extends TestServices {

    @Test
    public void testUnwrap() throws InvalidInputException {
        Message sampleMessage = sampleMessage();
        assertEquals(
                sampleMessage,
                receiver.unwrap(sequence(
                senderSigningKey.getPublicKey(), 
                    signed(senderSigningKey, new Action(sampleMessage)))));
    }

    @Test
    public void testWrapUnwrap() throws InvalidInputException {
        Message sampleMessage = sampleMessage();
        assertEquals(
                sampleMessage,
                receiver.unwrap(sender.addressedEnvelope(sampleMessage)));
    }
}
