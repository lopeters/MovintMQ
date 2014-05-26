package movint.mq.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.TextMessage;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 22:45
 */
public class MovintMQMessage implements TextMessage {
	private Map<String, String> headers;
	private String body;

	public MovintMQMessage(Map<String, String> headers, String body) {
		this.headers = headers;
		this.body = body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public String getJMSMessageID() throws JMSException {
		return null;
	}

	@Override
	public void setJMSMessageID(String id) throws JMSException {

	}

	@Override
	public long getJMSTimestamp() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSTimestamp(long timestamp) throws JMSException {

	}

	@Override
	public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
		return new byte[0];
	}

	@Override
	public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException {

	}

	@Override
	public void setJMSCorrelationID(String correlationID) throws JMSException {

	}

	@Override
	public String getJMSCorrelationID() throws JMSException {
		return null;
	}

	@Override
	public Destination getJMSReplyTo() throws JMSException {
		return null;
	}

	@Override
	public void setJMSReplyTo(Destination replyTo) throws JMSException {

	}

	@Override
	public Destination getJMSDestination() throws JMSException {
		return null;
	}

	@Override
	public void setJMSDestination(Destination destination) throws JMSException {

	}

	@Override
	public int getJMSDeliveryMode() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSDeliveryMode(int deliveryMode) throws JMSException {

	}

	@Override
	public boolean getJMSRedelivered() throws JMSException {
		return false;
	}

	@Override
	public void setJMSRedelivered(boolean redelivered) throws JMSException {

	}

	@Override
	public String getJMSType() throws JMSException {
		return null;
	}

	@Override
	public void setJMSType(String type) throws JMSException {

	}

	@Override
	public long getJMSExpiration() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSExpiration(long expiration) throws JMSException {

	}

	@Override
	public long getJMSDeliveryTime() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSDeliveryTime(long deliveryTime) throws JMSException {

	}

	@Override
	public int getJMSPriority() throws JMSException {
		return 0;
	}

	@Override
	public void setJMSPriority(int priority) throws JMSException {

	}

	@Override
	public void clearProperties() throws JMSException {

	}

	@Override
	public boolean propertyExists(String name) throws JMSException {
		return false;
	}

	@Override
	public boolean getBooleanProperty(String name) throws JMSException {
		return false;
	}

	@Override
	public byte getByteProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public short getShortProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public int getIntProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public long getLongProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public float getFloatProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public double getDoubleProperty(String name) throws JMSException {
		return 0;
	}

	@Override
	public String getStringProperty(String name) throws JMSException {
		return null;
	}

	@Override
	public Object getObjectProperty(String name) throws JMSException {
		return null;
	}

	@Override
	public Enumeration getPropertyNames() throws JMSException {
		return null;
	}

	@Override
	public void setBooleanProperty(String name, boolean value) throws JMSException {

	}

	@Override
	public void setByteProperty(String name, byte value) throws JMSException {

	}

	@Override
	public void setShortProperty(String name, short value) throws JMSException {

	}

	@Override
	public void setIntProperty(String name, int value) throws JMSException {

	}

	@Override
	public void setLongProperty(String name, long value) throws JMSException {

	}

	@Override
	public void setFloatProperty(String name, float value) throws JMSException {

	}

	@Override
	public void setDoubleProperty(String name, double value) throws JMSException {

	}

	@Override
	public void setStringProperty(String name, String value) throws JMSException {

	}

	@Override
	public void setObjectProperty(String name, Object value) throws JMSException {

	}

	@Override
	public void acknowledge() throws JMSException {

	}

	@Override
	public void clearBody() throws JMSException {
		this.body = null;
	}

	@Override
	public <T> T getBody(Class<T> messageType) throws JMSException {
		if (messageType == null) {
			throw new IllegalArgumentException("Message type must not be null");
		}
		if (!isBodyAssignableTo(messageType)) {
			throw new MessageFormatException("Unsupported message type - " + messageType.getSimpleName());
		}
		return (T) body;
	}

	@Override
	public boolean isBodyAssignableTo(Class messageType) throws JMSException {
		return String.class.isAssignableFrom(messageType);
	}

	@Override
	public void setText(String body) throws JMSException {
		this.body = body;
	}

	@Override
	public String getText() throws JMSException {
		return body;
	}
}
