package movint.mq.api.frame.header;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:49
 */
public class SubscriptionId {
	private final String value;

	public SubscriptionId(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
