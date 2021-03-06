package movint.mq.api.stomp.frame;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Collections;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:31
 */
public class Frame {
	private final StompCommand command;
	private final Map<String, String> headers;
	private final String body;

	public Frame(StompCommand command, Map<String, String> headers) {
		this(command, headers, "");
	}

	public Frame(StompCommand command, Map<String, String> headers, String body) {
		if (command == null) {
			throw new IllegalArgumentException("StompCommand must not be null");
		}
		this.command = command;
		this.headers = headers != null ? headers : Collections.<String, String>emptyMap();
		this.body = body != null ? body : "";
	}

	public StompCommand getCommand() {
		return command;
	}

	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	public String getBody() {
		return body;
	}

	public String serialize() {
		return new FrameSerializer().convertToWireFormat(this);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public boolean equals(Object tother) {
		return EqualsBuilder.reflectionEquals(this, tother);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
