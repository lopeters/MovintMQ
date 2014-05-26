package movint.mq.api.stomp.frame.builders;

import movint.mq.api.stomp.frame.ClientCommand;
import movint.mq.api.stomp.frame.Frame;

import java.util.LinkedHashMap;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class ConnectFrameBuilder implements FrameBuilder {
	public static final String ACCEPT_VERSION_HEADER = "accept-version";
	public static final String HOST_HEADER = "host";

	private final String host;
	private final String[] acceptedVersions;
	private final ClientCommand command;

	public static ConnectFrameBuilder connectFrameBuilder(String host, String... acceptedVersions) {
		return new ConnectFrameBuilder(ClientCommand.CONNECT, host, acceptedVersions);
	}

	public static ConnectFrameBuilder stompFrameBuilder(String host, String... acceptedVersions) {
		return new ConnectFrameBuilder(ClientCommand.STOMP, host, acceptedVersions);
	}

	public ConnectFrameBuilder(String host, String... acceptedVersions) {
		this(ClientCommand.CONNECT, host, acceptedVersions);
	}

	private ConnectFrameBuilder(ClientCommand command, String host, String... acceptedVersions) {
		this.command = command;
		this.host = host;
		if (acceptedVersions.length == 0) {
			throw new IllegalArgumentException("You must specify at least one accepted version");
		}
		this.acceptedVersions = acceptedVersions;
	}

	public Frame build() {
		return new Frame(command, new LinkedHashMap<String, String>() {{
			put(ACCEPT_VERSION_HEADER, join(acceptedVersions, ","));
			put(HOST_HEADER, host);
		}});
	}
}
