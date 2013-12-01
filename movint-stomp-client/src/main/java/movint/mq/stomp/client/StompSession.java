package movint.mq.stomp.client;

import movint.mq.stomp.client.connection.Connection;
import movint.mq.stomp.client.connection.ConnectionFactory;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.ReceivedFrameHandler;
import movint.mq.stomp.client.frame.builders.ConnectFrameBuilder;
import movint.mq.stomp.client.frame.builders.DisconnectFrameBuilder;
import movint.mq.stomp.client.frame.header.SequentialIdGenerator;

import java.io.IOException;

public class StompSession implements ReceivedFrameHandler {
	private static final String[] ACCEPTED_VERSIONS = new String[]{"1.0", "1.1", "1.2"};

	private final SequentialIdGenerator idGenerator = new SequentialIdGenerator();
	private final Connection connection;

	private StompSession(Connection connection) {
		this.connection = connection;
	}

	public static StompSession create(ConnectionFactory connectionFactory) throws IOException {
		Connection connection = connectionFactory.newConnection();
		StompSession stompSession = new StompSession(connection);
		connection.addReceivedFrameHandler(stompSession);
		connection.send(new ConnectFrameBuilder(connectionFactory.getHost(), ACCEPTED_VERSIONS).build());
		return stompSession;
	}

	public void destroy() throws IOException {
		connection.send(new DisconnectFrameBuilder().withReceiptId(idGenerator).build());
		connection.close();
	}

	@Override
	public void frameReceived(Frame frame) {
	}
}
