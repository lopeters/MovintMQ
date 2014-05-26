package movint.mq.api.stomp.frame;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 10/07/13
 * Time: 00:44
 */
public class StreamingFrameParserTest {

	public static final String STOMP_FRAME_NO_BODY = "STOMP\naccept-version:1.2\nhost:localhost\n\n\0";
	public static final String ERROR_FRAME_NO_HEADERS = "ERROR\n\nThe message:\n\n'hello mum!' was malformed\0";
	public static final String ERROR_FRAME_NO_HEADERS_AND_EOLS_AFTER_NULL = ERROR_FRAME_NO_HEADERS + "\n\n\n\n\n\n\n";
	public static final String ERROR_FRAME_WITH_NULL_IN_BODY = "ERROR\ncontent-length:41\n\nThe message:\n\n'hello mum!\0' was malformed\0";
	public static final String SEND_FRAME_WITH_HEADERS_AND_BODY = "SEND\ndestination:/queue/foo\n\nhello mum\0";
	public static final String SEND_FRAME_WITH_UTF8_CHARACTERS = "SEND\ndestination:/queue/foo\n\nγεια σου μαμα\0";

	private final StreamingFrameParser underTest = new StreamingFrameParser(clientAndServerCommandFactory());

	@Test
	public void parseFrameWithHeadersAndBody() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(SEND_FRAME_WITH_HEADERS_AND_BODY));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo"), "hello mum"), actualFrame);
	}

	@Test
	public void canParseMultipleFramesFromTheSameReader() throws IOException {
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);
		new Thread(() -> {
			try {
				out.write(STOMP_FRAME_NO_BODY.getBytes());
				out.flush();
				Thread.sleep(2000);
				out.write(SEND_FRAME_WITH_HEADERS_AND_BODY.getBytes());
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Frame firstFrame = underTest.parse(reader);
		assertEquals(new Frame(ClientCommand.STOMP, new LinkedHashMap<String, String>() {{
			put("accept-version", "1.2");
			put("host", "localhost");
		}}, ""), firstFrame);
		Frame secondFrame = underTest.parse(reader);
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo"), "hello mum"), secondFrame);
	}

	@Test
	public void parseFrameWithLargeBody() throws IOException {
		String largeBody = StringUtils.repeat("hello mum", 1000);
		Frame actualFrame = underTest.parse(readerFor(String.format("SEND\ndestination:/queue/foo\n\n%s\0", largeBody)));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo"), largeBody), actualFrame);
	}

	@Test
	public void parseFrameWithUTF8Characters() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(SEND_FRAME_WITH_UTF8_CHARACTERS));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo"), "γεια σου μαμα"), actualFrame);
	}

	@Test
	public void parseFrameWithNoBody() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(STOMP_FRAME_NO_BODY));
		assertEquals(new Frame(ClientCommand.STOMP, new LinkedHashMap<String, String>() {{
			put("accept-version", "1.2");
			put("host", "localhost");
		}}, ""), actualFrame);
	}

	@Test
	public void parseFrameWithNoHeaders() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(ERROR_FRAME_NO_HEADERS));
		assertEquals(new Frame(ServerCommand.ERROR, Collections.<String, String>emptyMap(), "The message:\n\n'hello mum!' was malformed"), actualFrame);
	}

	@Test
	public void parseFrameWitEOLsAfterNullCharacter() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(ERROR_FRAME_NO_HEADERS_AND_EOLS_AFTER_NULL));
		assertEquals(new Frame(ServerCommand.ERROR, Collections.<String, String>emptyMap(), "The message:\n\n'hello mum!' was malformed"), actualFrame);
	}

	@Test
	public void parseFrameWithContentLengthHeaderAndNullsInBody() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(ERROR_FRAME_WITH_NULL_IN_BODY));

		String body = "The message:\n\n'hello mum!\0' was malformed";
		assertEquals(new Frame(ServerCommand.ERROR, Collections.singletonMap("content-length", String.valueOf(body.length())), body), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedColonInHeaders() throws IOException {
		Frame actualFrame = underTest.parse(readerFor("SEND\ndestination:/queue/foo\\cbar\n\nhello mum\0"));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo:bar"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedLineFeedInHeaders() throws IOException {
		Frame actualFrame = underTest.parse(readerFor("SEND\ndestination:/queue/foo\\rbar\n\nhello mum\0"));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo\nbar"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedCarriageReturnInHeaders() throws IOException {
		Frame actualFrame = underTest.parse(readerFor("SEND\ndestination:/queue/foo\\nbar\n\nhello mum\0"));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo\nbar"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedSlashInHeaders() throws IOException {
		Frame actualFrame = underTest.parse(readerFor("SEND\ndestination:/queue/foo\\\\bar\n\nhello mum\0"));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo\\bar"), "hello mum"), actualFrame);
	}

	@Test
	public void parsingEmptyReaderReturnsNull() throws IOException {
		assertNull(underTest.parse(new BufferedReader(new StringReader(""))));
	}

	@Test
	public void discardEmptyCommandLineAndReadNextFrame() throws IOException {
		Frame actualFrame = underTest.parse(readerFor(System.lineSeparator() + SEND_FRAME_WITH_HEADERS_AND_BODY));
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo"), "hello mum"), actualFrame);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFromNullReader() throws IOException {
		underTest.parse(null);
	}

	@Test(expected = FrameFormatException.class)
	public void cannotParseFrameWithNotEnoughLines() throws IOException {
		underTest.parse(readerFor("STOMP\n\0"));
	}

	@Test(expected = FrameFormatException.class)
	public void cannotParseFrameWithInvalidHeader() throws IOException {
		underTest.parse(readerFor("STOMP\nhost:localhost:foo\n\n\0"));
	}

	@Test(expected = FrameFormatException.class)
	public void cannotParseFrameWithNoNewLineBetweenHeadersAndBody() throws IOException {
		underTest.parse(readerFor("STOMP\nhost:localhost\nhello mum\0"));
	}

	@Test(expected = FrameFormatException.class)
	public void cannotParseFrameWithContentLengthHeaderThatIsTooLarge() throws IOException {
		underTest.parse(readerFor("STOMP\ncontent-length:100\nhello mum\0"));
	}

	@Test(expected = FrameFormatException.class)
	public void cannotParseFrameWithContentLengthHeaderThatIsTooSmall() throws IOException {
		underTest.parse(readerFor("STOMP\ncontent-length:1\nhello mum\0"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithUnrecognisedCommand() throws IOException {
		underTest.parse(readerFor("WANG\n\nhello mum\0"));
	}

	@Test(expected = FrameFormatException.class)
	public void cannotParseFrameThatIsNotTerminatedByNull() throws IOException {
		System.out.println(underTest.parse(readerFor("STOMP\n\n")));
	}

	private BufferedReader readerFor(String input) {
		return new BufferedReader(new StringReader(input));
	}

	private StompCommandFactory clientAndServerCommandFactory() {
		return new StompCommandFactory() {
			@Override
			public StompCommand createCommand(String commandName) {
				try {
					return ClientCommand.valueOf(commandName);
				} catch (Exception e) {
					return ServerCommand.valueOf(commandName);
				}
			}
		};
	}
}
