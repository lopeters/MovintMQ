package movint.mq.api.stomp.frame;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:31
 */
public class FrameSerializer {
	public String convertToWireFormat(Frame frame) {
		if (frame == null) {
			throw new IllegalArgumentException("Cannot serialize null Frame");
		}
		StringBuilder payload = new StringBuilder();
		payload.append(frame.getCommand().name()).append("\n");
		Map<String, String> headers = frame.getHeaders();
		for (String key : headers.keySet()) {
			payload.append(escape(key)).append(":").append(escape(headers.get(key))).append("\n");
		}
		payload.append("\n").append(frame.getBody()).append("\0");
		return payload.toString();
	}

	private String escape(String headerString) {
		return headerString.replace("\\", "\\\\").replace(":", "\\c").replace("\n", "\\n").replace("\r", "\\r");
	}
}
