package movint.mq.stomp.client;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:49
 */
public class MessageId {
	private final String value;

	public MessageId(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
