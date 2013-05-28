package movint.mq.stomp.client;

import org.junit.Test;

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

	private final Message message = new Message(singletonMap("name", "value"), "Hello mum");

	@Test
	public void sendsMessage() {
		ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
		Connection connection = mock(Connection.class);
		when(connectionFactory.connect()).thenReturn(connection);
		StompProducer underTest = new StompProducer(connectionFactory);

		underTest.sendTo(queue("foo"), message);

		verify(connection).send(queue("foo"), message);
		verify(connection).disconnect();
	}
}
