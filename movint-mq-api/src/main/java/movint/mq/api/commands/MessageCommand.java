package movint.mq.api.commands;

import movint.mq.api.Destination;

import java.util.Map;

public class MessageCommand implements Command {
	private Destination destination;
	private Map<String, String> headers;
	private String body;

	public MessageCommand(Destination destination, Map<String, String> headers, String body) {
		this.destination = destination;
		this.headers = headers;
		this.body = body;
	}

	public Destination getDestination() {
		return destination;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}
}
