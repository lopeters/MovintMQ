package movint.mq.stomp.client.frame.builders;

import movint.mq.stomp.client.frame.Command;
import movint.mq.stomp.client.frame.Frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class BeginFrameBuilder {
	public Frame build() {
		return new Frame(Command.BEGIN, null, null);
	}
}
