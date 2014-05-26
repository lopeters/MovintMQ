package movint.mq.client.transport;

import movint.mq.api.commands.Command;
import movint.mq.api.stomp.frame.CommandListener;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public interface Transport {
	void send(Command command) throws IOException;

	void close() throws IOException;

	void setCommandListener(CommandListener commandListener);
}
