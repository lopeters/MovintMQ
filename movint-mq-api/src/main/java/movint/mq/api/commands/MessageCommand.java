package movint.mq.api.commands;

import movint.mq.api.Destination;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

public class MessageCommand implements Command {
    private final Destination destination;
    private final Map<String, String> headers;
    private final String body;

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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
