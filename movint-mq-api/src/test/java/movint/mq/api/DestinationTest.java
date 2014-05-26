package movint.mq.api;

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

	@Test
	public void parseQueueFromDestinationString() throws MalformedURLException {
		assertEquals(Destination.queue("foo/bar"), Destination.parse("/queue/foo/bar"));
	}

	@Test
	public void parseTopicFromDestinationString() throws MalformedURLException {
		assertEquals(Destination.topic("foo/bar"), Destination.parse("/topic/foo/bar"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseUnknownDestinationType() throws MalformedURLException {
		Destination.parse("/spoon/foo/bar");
	}
}
