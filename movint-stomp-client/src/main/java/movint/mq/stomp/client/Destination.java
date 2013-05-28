package movint.mq.stomp.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:53
 */
public class Destination {
	private final DestinationType type;
	private final String name;

	public enum DestinationType {
		queue, topic
	}

	Destination(DestinationType type, String name) {
		this.type = type;
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Destination name cannot be blank");
		}
		this.name = name;
	}

	public static Destination topic(String name) {
		return new Destination(DestinationType.topic, name);
	}

	public static Destination queue(String name) {
		return new Destination(DestinationType.queue, name);
	}

	public String name() {
		return "/" + type.name() + "/" + name;
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}
}
