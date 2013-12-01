package movint.mq.stomp.client;

import movint.mq.stomp.client.connection.Connection;
import movint.mq.stomp.client.connection.ConnectionFactory;
import movint.mq.stomp.client.frame.ClientCommand;
import movint.mq.stomp.client.frame.Frame;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StompSessionTest {
	private final String HOST = "localhost";

	private final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
	private final Connection connection = Mockito.mock(Connection.class);

	@Before
	public void setupConnection() {
		when(connectionFactory.getHost()).thenReturn(HOST);
		when(connectionFactory.newConnection()).thenReturn(connection);
	}

	@Test
	public void createSessionCreatesANewConnectionAndSendsConnectFrame() throws IOException {
		StompSession underTest = StompSession.create(connectionFactory);
		verify(connection).addReceivedFrameHandler(underTest);
		verify(connection).send(expectedConnectFrame());
	}

	@Test
	public void destroySessionSendsDisconnectFrameAndClosesConnection() throws IOException {
		StompSession underTest = StompSession.create(connectionFactory);
		underTest.destroy();
		verify(connection).send(expectedDisconnectFrame());
		verify(connection).close();
	}

	private Frame expectedConnectFrame() {
		return new Frame(ClientCommand.CONNECT, new LinkedHashMap<String, String>() {{
			put("accept-version", "1.0,1.1,1.2");
			put("host", HOST);
		}}, null);
	}


	private Frame expectedDisconnectFrame() {
		return new Frame(ClientCommand.DISCONNECT, new LinkedHashMap<String, String>() {{
			put("receipt", "1");
		}}, null);
	}
}

