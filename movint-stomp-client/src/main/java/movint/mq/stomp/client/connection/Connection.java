package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.Frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public interface Connection {
	void open();

	void send(Frame frame);

	void close();

	// TODO - should this live here? would it be better on ConnectionFactory or as part of some 'Properties'-like object?
	String host();
}
