package movint.mq.stomp.api;

import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:02
 */
public class DestinationTest {
	@Test
	public void createsTopicWithCorrectDestination() throws MalformedURLException {
		assertEquals("/topic/foo", Destination.topic("foo").value());
	}

	@Test
	public void createsQueueWithCorrectDestination() throws MalformedURLException {
		assertEquals("/queue/foo", Destination.queue("foo").value());
	}
}
