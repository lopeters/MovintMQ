package movint.mq.stomp.api.frame.builders;

import movint.mq.stomp.api.frame.ClientCommand;
import movint.mq.stomp.api.frame.Frame;
import movint.mq.stomp.api.frame.header.MessageId;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class NotAcknowledgedFrameBuilder {
	private final MessageId messageId;

	public NotAcknowledgedFrameBuilder(MessageId messageId) {

		this.messageId = messageId;
	}

	public Frame build() {
		return new Frame(ClientCommand.NACK, null, null);
	}
}
