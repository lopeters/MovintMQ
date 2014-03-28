package movint.mq.api.frame.builders;

import movint.mq.api.frame.ClientCommand;
import movint.mq.api.frame.Frame;
import movint.mq.api.frame.header.MessageId;

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
		return new Frame(ClientCommand.ACK, null, null);
	}
}
