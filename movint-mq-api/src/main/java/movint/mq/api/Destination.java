package movint.mq.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static org.apache.commons.lang3.StringUtils.isBlank;

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
		if (isBlank(name)) {
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

	public static Destination parse(String destinationString) {
		String[] split = destinationString.split("/", 3);
		return new Destination(DestinationType.valueOf(split[1]), split[2]);
	}

	public String value() {
		return "/" + type.name() + "/" + name;
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
