package movint.mq.stomp.client;

import movint.mq.stomp.client.connection.Connection;
import movint.mq.stomp.client.connection.ConnectionFactory;
import movint.mq.stomp.client.frame.builders.ConnectFrameBuilder;
import movint.mq.stomp.client.frame.builders.DisconnectFrameBuilder;
import movint.mq.stomp.client.frame.builders.SendFrameBuilder;
import movint.mq.stomp.client.frame.header.SequentialIdGenerator;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:38
 */
public class StompProducer {
	private static final String[] ACCEPTED_VERSIONS = new String[]{"1.0", "1.1", "1.2"};

	private final SequentialIdGenerator idGenerator = new SequentialIdGenerator();
	private final ConnectionFactory connectionFactory;

	public StompProducer(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void sendTo(Destination destination, Message message) {
		Connection connection = connectionFactory.newConnection();
		connection.open()
				.send(new ConnectFrameBuilder(connectionFactory.getHost(), ACCEPTED_VERSIONS).build())
				.send(new SendFrameBuilder(destination, message).build())
				.send(new DisconnectFrameBuilder().withReceiptId(idGenerator).build())
				.close();
	}
}
