package movint.mq.stomp.client.frame.builders;

import movint.mq.stomp.client.frame.Command;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.header.MessageId;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class AcknowledgedFrameBuilder {
	private final MessageId messageId;

	public AcknowledgedFrameBuilder(MessageId messageId) {
		this.messageId = messageId;
	}

	public Frame build() {
		return new Frame(Command.ACK, null, null);
	}
}
