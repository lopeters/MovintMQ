package movint.mq.stomp.client.frame;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 02/06/13
 * Time: 01:19
 */
public class FrameParser {
	public Frame parse(String frameText) {
		if (StringUtils.isBlank(frameText)) {
			throw new IllegalArgumentException("Cannot parse blank frame");
		}
		String[] frameSections = frameText.split("\n\n", 2);
		if (frameSections.length < 2) {
			throw new IllegalArgumentException("Invalid frame - no end of line found after command and headers section: " + frameText);
		}
		if (!frameSections[1].contains("\0")) {
			throw new IllegalArgumentException("Invalid frame - not terminated by a null character: " + frameSections[1]);
		}
		String[] commandAndHeaders = frameSections[0].split("\n");
		String body = frameSections[1];
		Command command = Command.valueOf(commandAndHeaders[0]);
		Map<String, String> headers = parseHeaders(commandAndHeaders);
		return new Frame(command, headers, body.substring(0, body.lastIndexOf("\0")));
	}

	private Map<String, String> parseHeaders(String[] commandAndHeaders) {
		Map<String, String> headers = new LinkedHashMap<>();
		List<String> headerList = new ArrayList<>(Arrays.asList(commandAndHeaders));
		headerList.remove(0);
		for (String header : headerList) {
			String[] keyValuePair = header.split(":", 2);
			headers.put(keyValuePair[0], keyValuePair[1]);
		}
		return headers;
	}
}
