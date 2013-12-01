package movint.mq.stomp.client.frame;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 02/06/13
 * Time: 01:19
 */
public class StreamingFrameParser implements FrameParser<BufferedReader> {
	private static final int BUFFER_SIZE = 1500;

	private final CommandFactory commandFactory;

	public StreamingFrameParser(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	@Override
	public Frame parse(BufferedReader reader) throws IOException {
		if (reader == null)
			throw new IllegalArgumentException("Cannot parse null input stream");
		Map<String, String> headers = new LinkedHashMap<>();
		Command command = null;
		String currentInput;
		boolean readingCommandLine = true;
		while ((currentInput = reader.readLine()) != null && currentInput.length() > 0) {
			if (readingCommandLine) {
				readingCommandLine = false;
				command = commandFactory.createCommand(currentInput);
			} else {
				String[] keyValuePair = currentInput.split(":");
				if (keyValuePair.length != 2)
					throw new FrameFormatException("Invalid header: '" + StringUtils.join(keyValuePair, ":") + "'");
				headers.put(unescape(keyValuePair[0]), unescape(keyValuePair[1]));
			}
		}
		if (command == null)
			return null;
		else if (currentInput == null)
			throw new FrameFormatException("Stream terminated before end of frame");
		String contentLengthHeader = headers.get("content-length");
		return new Frame(command, headers, readBody(reader, contentLengthHeader));
	}

	private String readBody(BufferedReader reader, String contentLengthHeader) throws IOException {
		if (contentLengthHeader != null) {
			return readSpecifiedContentLength(reader, Integer.valueOf(contentLengthHeader));
		} else {
			return readUpToNullCharacter(reader);
		}
	}

	private String readUpToNullCharacter(BufferedReader reader) throws IOException {
		StringBuilder body = new StringBuilder();
		int nullIndex = -1;
		char[] inputBuffer = new char[BUFFER_SIZE];
		while (nullIndex < 0 && reader.read(inputBuffer, 0, BUFFER_SIZE) >= 0) {
			String input = new String(inputBuffer);
			nullIndex = input.indexOf("\0");
			body.append(nullIndex == -1 ? input : input.substring(0, nullIndex));
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
