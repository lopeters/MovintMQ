package movint.mq.api.stomp.frame.builders;

import movint.mq.api.stomp.frame.ClientCommand;
import movint.mq.api.stomp.frame.Frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class AbortFrameBuilder {
	public Frame build() {
		return new Frame(ClientCommand.ABORT, null, null);
	}
}
