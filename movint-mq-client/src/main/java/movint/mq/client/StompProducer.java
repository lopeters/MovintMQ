package movint.mq.client;

import movint.mq.api.Destination;
import movint.mq.api.Message;
import movint.mq.api.stomp.frame.builders.SendFrameBuilder;
import movint.mq.client.connection.Connection;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:38
 */
public class StompProducer {
	private final Connection connection;

	public StompProducer(Connection connection) {
		this.connection = connection;
	}

	public void sendTo(Destination destination, Message message) throws IOException {
		connection.send(new SendFrameBuilder(destination, message).build());
	}
}
