package movint.mq.stomp.client;

import movint.mq.stomp.client.connection.Connection;
import movint.mq.stomp.client.connection.ConnectionFactory;
import movint.mq.stomp.client.frame.Command;
import movint.mq.stomp.client.frame.Frame;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static java.util.Collections.singletonMap;
import static movint.mq.stomp.client.Destination.queue;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:41
 */
public class StompProducerTest {
	private static final String HOST = "localhost";
	private static final String MESSAGE_BODY = "Hello mum";
	private static final String QUEUE_NAME = "foo";

	private final Message message = new Message(singletonMap("name", "value"), MESSAGE_BODY);
	private final ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
	private final Connection connection = mock(Connection.class);
	private final StompProducer underTest = new StompProducer(connectionFactory);

	@Before
	public void setUp() {
		when(connectionFactory.newConnection()).thenReturn(connection);
		when(connectionFactory.getHost()).thenReturn(HOST);
		when(connection.send(any(Frame.class))).thenReturn(connection);
		when(connection.open()).thenReturn(connection);
		when(connection.close()).thenReturn(connection);
	}

	@Test
	public void sendsMessage() {
		underTest.sendTo(queue(QUEUE_NAME), message);

		verify(connection).open();
		verify(connection).send(expectedConnectFrame());
		verify(connection).send(expectedSendFrame());
		verify(connection).send(expectedDisconnectFrame());
		verify(connection).close();
	}

	private Frame expectedConnectFrame() {
		return new Frame(Command.CONNECT, new LinkedHashMap<String, String>() {{
			put("accept-version", "1.0,1.1,1.2");
			put("host", "localhost");
		}}, null);
	}

	private Frame expectedSendFrame() {
		return new Frame(Command.SEND, new LinkedHashMap<String, String>() {{
			put("destination", "/queue/" + QUEUE_NAME);
			put("name", "value");
		}}, MESSAGE_BODY);
	}

	private Frame expectedDisconnectFrame() {
		return new Frame(Command.DISCONNECT, new LinkedHashMap<String, String>() {{
			put("receipt", "1");
		}}, null);
	}
}
