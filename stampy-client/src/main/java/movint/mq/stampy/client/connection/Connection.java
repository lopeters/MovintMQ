package movint.mq.stampy.client.connection;

import movint.mq.stomp.api.frame.Frame;
import movint.mq.stomp.api.frame.ReceivedFrameHandler;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public interface Connection {
	void send(Frame frame) throws IOException;

	void close() throws IOException;

	void addReceivedFrameHandler(ReceivedFrameHandler receivedFrameHandler);
}
