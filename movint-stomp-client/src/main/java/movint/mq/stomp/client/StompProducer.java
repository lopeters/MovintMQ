package movint.mq.stomp.client;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:38
 */
public class StompProducer {
	private final ConnectionFactory connectionFactory;

	public StompProducer(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public void sendTo(Destination destination, Message message) {
		Connection connection = connectionFactory.connect();
		connection.send(destination, message);
		connection.disconnect();
	}
}
