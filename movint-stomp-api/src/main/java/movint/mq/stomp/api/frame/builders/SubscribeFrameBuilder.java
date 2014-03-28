package movint.mq.stomp.api.frame.builders;

import movint.mq.stomp.api.frame.ClientCommand;
import movint.mq.stomp.api.frame.Frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class SubscribeFrameBuilder {
	public Frame build() {
		return new Frame(ClientCommand.SUBSCRIBE, null, null);
	}
}
