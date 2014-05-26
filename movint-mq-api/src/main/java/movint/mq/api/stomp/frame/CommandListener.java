package movint.mq.api.stomp.frame;

import movint.mq.api.commands.Command;

public interface CommandListener {
	void onCommand(Command command);
}
