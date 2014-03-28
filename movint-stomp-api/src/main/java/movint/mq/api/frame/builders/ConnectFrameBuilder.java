package movint.mq.api.frame.builders;

import movint.mq.api.frame.ClientCommand;
import movint.mq.api.frame.Frame;

import java.util.LinkedHashMap;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class ConnectFrameBuilder {
	public static final String ACCEPT_VERSION_HEADER = "accept-version";
	public static final String HOST_HEADER = "host";

	private final String host;
	private final String[] acceptedVersions;

	public ConnectFrameBuilder(String host, String... acceptedVersions) {
		this.host = host;
		if (acceptedVersions.length == 0) {
			throw new IllegalArgumentException("You must specify at least one accepted version");
		}
		this.acceptedVersions = acceptedVersions;
	}

	public Frame build() {
		return new Frame(ClientCommand.CONNECT, new LinkedHashMap<String, String>() {{
			put(ACCEPT_VERSION_HEADER, join(acceptedVersions, ","));
			put(HOST_HEADER, host);
		}}, null);
	}
}
