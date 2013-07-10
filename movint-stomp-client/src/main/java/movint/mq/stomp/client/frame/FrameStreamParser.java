package movint.mq.stomp.client.frame;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 02/06/13
 * Time: 01:19
 */
public class FrameStreamParser {
	private final CommandFactory commandFactory;

	public FrameStreamParser(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	public Frame parse(InputStream stream) throws IOException {
		if (stream == null) {
			throw new IllegalArgumentException("Cannot parse null input stream");
		}
		Map<String, String> headers = new LinkedHashMap<>();
		Command command = null;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
			String currentInput;
			boolean readingCommandLine = true;
			while ((currentInput = in.readLine()) != null && currentInput.length() > 0) {
				if (readingCommandLine) {
					readingCommandLine = false;
					command = commandFactory.createCommand(currentInput);
				} else {
					String[] keyValuePair = currentInput.split(":");
					if (keyValuePair.length != 2) {
						throw new FrameFormatException("Invalid header: '" + StringUtils.join(keyValuePair, ":") + "'");
					}
					headers.put(unescape(keyValuePair[0]), unescape(keyValuePair[1]));
				}
			}
			if (currentInput == null) {
				throw new FrameFormatException("Stream terminated before end of frame");
			}
			String contentLengthHeader = headers.get("content-length");
			return new Frame(command, headers, readBody(in, contentLengthHeader));
		}
	}

	private String readBody(BufferedReader in, String contentLengthHeader) throws IOException {
		if (contentLengthHeader != null) {
			return readSpecifiedContentLength(in, Integer.valueOf(contentLengthHeader));
		} else {
			return readUpToNullCharacter(in);
		}
	}

	private String readUpToNullCharacter(BufferedReader in) throws IOException {
		String currentInput;
		StringBuilder body = new StringBuilder();
		boolean firstLine = true;
		int nullIndex = -1;
		while (nullIndex < 0 && (currentInput = in.readLine()) != null) {
			if (firstLine) firstLine = false;
			else body.append("\n");
			nullIndex = currentInput.indexOf("\0");
			body.append(nullIndex >= 0 ? currentInput.substring(0, nullIndex) : currentInput);
		}
		if (nullIndex == -1) {
			throw new FrameFormatException("Frame was not terminated by null character: " + body.toString());
		}
		return body.toString();
	}

	private String readSpecifiedContentLength(BufferedReader in, Integer contentLength) throws IOException {
		char[] inputBuffer = new char[contentLength];
		in.read(inputBuffer, 0, contentLength);
		int terminationCharacter = in.read();
		if (terminationCharacter != 0) {
			throw new FrameFormatException("Frame was not terminated by null character: " + new String(inputBuffer));
		}
		return new String(inputBuffer);
	}

	private String unescape(String value) {
		return value.replace("\\c", ":").replace("\\n", "\n").replace("\\r", "\n").replace("\\\\", "\\");
	}
}
