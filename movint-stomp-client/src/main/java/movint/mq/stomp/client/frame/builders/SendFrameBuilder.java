package movint.mq.stomp.client.frame.builders;

import movint.mq.stomp.client.Destination;
import movint.mq.stomp.client.Message;
import movint.mq.stomp.client.frame.Command;
import movint.mq.stomp.client.frame.Frame;

import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class SendFrameBuilder {
	public static final String DESTINATION_HEADER = "destination";

	private final Destination destination;
	private final Message message;

	public SendFrameBuilder(Destination destination, Message message) {
		if (destination == null) {
			throw new IllegalArgumentException("Destination cannot be null");
		}
		if (message == null) {
			throw new IllegalArgumentException("Message cannot be null");
		}
		this.destination = destination;
		this.message = message;
	}

	public Frame build() {
		return new Frame(Command.SEND, new LinkedHashMap<String, String>() {{
			putAll(message.getHeaders());
			put(DESTINATION_HEADER, destination.value());
		}}, message.getBody());
	}
}
