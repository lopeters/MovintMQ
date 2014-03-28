package movint.mq.stampy.client;

import movint.mq.stampy.client.connection.Connection;
import movint.mq.stomp.api.Destination;
import movint.mq.stomp.api.Message;
import movint.mq.stomp.api.frame.builders.SendFrameBuilder;

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
