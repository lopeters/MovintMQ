package movint.mq.stomp.client.frame;

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
	private final Command command;
	private final Map<String, String> headers;
	private final String body;

	public Frame(Command command, Map<String, String> headers, String body) {
		this.command = command;
		this.headers = headers;
		this.body = body;
	}

	public Command getCommand() {
		return command;
	}

	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(headers);
	}

	public String getBody() {
		return body;
	}

	public byte[] getBytes() {
		return null;
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
