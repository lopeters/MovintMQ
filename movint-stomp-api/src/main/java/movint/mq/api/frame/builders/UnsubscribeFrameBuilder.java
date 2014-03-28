package movint.mq.api.frame.builders;

import movint.mq.api.frame.ClientCommand;
import movint.mq.api.frame.Frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class UnsubscribeFrameBuilder {
	public Frame build() {
		return new Frame(ClientCommand.UNSUBSCRIBE, null, null);
	}
}
