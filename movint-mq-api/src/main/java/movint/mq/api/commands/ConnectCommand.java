package movint.mq.api.commands;

import java.util.Objects;

public class ConnectCommand implements Command {
	private String host;

	public ConnectCommand(String host) {
		this.host = Objects.requireNonNull(host, "Host cannot be null");
	}

	public String getHost() {
		return host;
	}
}
