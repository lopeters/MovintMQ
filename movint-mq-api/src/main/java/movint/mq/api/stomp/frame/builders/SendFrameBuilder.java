package movint.mq.api.stomp.frame.builders;

import movint.mq.api.Destination;
import movint.mq.api.stomp.frame.ClientCommand;
import movint.mq.api.stomp.frame.Frame;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class SendFrameBuilder implements FrameBuilder {
	public static final String DESTINATION_HEADER = "destination";

	private final Destination destination;
	private final Map<String, String> headers;
	private final String body;

	public SendFrameBuilder(Destination destination, Map<String, String> headers, String body) {
		this.destination = Objects.requireNonNull(destination, "Destination cannot be null");
		this.headers = headers;
		this.body = body;
	}

	public Frame build() {
		return new Frame(ClientCommand.SEND, new LinkedHashMap<String, String>() {{
			if (headers != null) putAll(headers);
			put(DESTINATION_HEADER, destination.value());
		}}, body);
	}
}
