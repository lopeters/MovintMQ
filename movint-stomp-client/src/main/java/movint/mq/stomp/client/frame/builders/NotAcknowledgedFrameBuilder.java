package movint.mq.stomp.client.frame.builders;

import movint.mq.stomp.client.frame.ClientCommand;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.header.MessageId;

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
