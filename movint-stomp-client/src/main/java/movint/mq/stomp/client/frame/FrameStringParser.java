package movint.mq.stomp.client.frame;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 02/06/13
 * Time: 01:19
 */
public class FrameStringParser {
	private final CommandFactory commandFactory;

	public FrameStringParser(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

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
		Map<String, String> headers = parseHeaders(commandAndHeaders);
		int bodyLength = headers.containsKey("content-length") ? Integer.valueOf(headers.get("content-length")) - 1 : frameSections[1].lastIndexOf("\0");
		return new Frame(commandFactory.createCommand(commandAndHeaders[0]), headers, frameSections[1].substring(0, bodyLength));
	}

	private Map<String, String> parseHeaders(String[] commandAndHeaders) {
		Map<String, String> headers = new LinkedHashMap<>();
		List<String> headerList = new ArrayList<>(Arrays.asList(commandAndHeaders));
		headerList.remove(0);
		for (String header : headerList) {
			String[] keyValuePair = header.split(":", 2);
			headers.put(unescape(keyValuePair[0]), unescape(keyValuePair[1]));
		}
		return headers;
	}

	private String unescape(String value) {
		return value.replace("\\c", ":").replace("\\n", "\n").replace("\\r", "\n").replace("\\\\", "\\");
	}
}
