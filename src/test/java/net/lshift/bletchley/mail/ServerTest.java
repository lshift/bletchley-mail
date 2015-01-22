package net.lshift.bletchley.mail;

import static org.junit.Assert.assertEquals;
import net.lshift.spki.InvalidInputException;
import net.lshift.spki.suiteb.ActionType;
import net.lshift.spki.suiteb.SequenceItem;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ServerTest extends TestServices {
    @Test
    public void testServerActions() throws InvalidInputException {
        SequenceItem message = sender.wrap(sampleMessage());
        assertEquals(
                senderServer.actions(Relay.class, message), 
                ImmutableList.of(new Relay(TEST_RECEIVER_SERVER)));
        assertEquals(senderServer.actions(ActionType.class, message).size(), 1);
        assertEquals(
                receiverServer.actions(Deliver.class, message),
                ImmutableList.of(new Deliver(TEST_RECEIVER)));
        assertEquals(receiverServer.actions(ActionType.class, message).size(), 1);
    }
}
