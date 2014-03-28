package movint.mq.stampy.client;

import movint.mq.stampy.client.connection.Connection;
import movint.mq.stomp.api.Message;
import movint.mq.stomp.api.frame.ClientCommand;
import movint.mq.stomp.api.frame.Frame;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;

import static java.util.Collections.singletonMap;
import static movint.mq.stomp.api.Destination.queue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:41
 */
public class StompProducerTest {
	private static final String MESSAGE_BODY = "Hello mum";
	private static final String QUEUE_NAME = "foo";

	private final Message message = new Message(singletonMap("name", "value"), MESSAGE_BODY);
	private final Connection connection = mock(Connection.class);
	private final StompProducer underTest = new StompProducer(connection);

	@Test
	public void sendsMessage() throws IOException {
		underTest.sendTo(queue(QUEUE_NAME), message);
		verify(connection).send(expectedSendFrame());
	}

	private Frame expectedSendFrame() {
		return new Frame(ClientCommand.SEND, new LinkedHashMap<String, String>() {{
			put("destination", "/queue/" + QUEUE_NAME);
			put("name", "value");
		}}, MESSAGE_BODY);
	}
}
