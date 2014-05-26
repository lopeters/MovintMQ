package movint.mq.api.stomp.frame.builders;

import movint.mq.api.stomp.frame.ClientCommand;
import movint.mq.api.stomp.frame.Frame;
import movint.mq.api.stomp.frame.header.MessageId;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class NotAcknowledgedFrameBuilder implements FrameBuilder {
	private final MessageId messageId;

	public NotAcknowledgedFrameBuilder(MessageId messageId) {
		this.messageId = messageId;
	}

	public Frame build() {
		return new Frame(ClientCommand.NACK, null, null);
	}
}
