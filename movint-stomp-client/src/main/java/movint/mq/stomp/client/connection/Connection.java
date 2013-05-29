package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.Frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public interface Connection {
	Connection open();

	Connection send(Frame frame);

	Connection close();
}
