package movint.mq.api.stomp.frame;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 02/06/13
 * Time: 01:23
 */
public class StringFrameParserTest {

	public static final String STOMP_FRAME_NO_BODY = "STOMP\naccept-version:1.2\nhost:localhost\n\n\0";
	public static final String ERROR_FRAME_NO_HEADERS = "ERROR\n\nThe message:\n\n'hello mum!' was malformed\0";
	public static final String ERROR_FRAME_WITH_NULL_IN_BODY = "ERROR\ncontent-length:42\n\nThe message:\n\n'hello mum!\0' was malformed\0";
	public static final String SEND_FRAME_WITH_HEADERS_AND_BODY = "SEND\ndestination:/queue/foo\n\nhello mum\0";

	private final StringFrameParser underTest = new StringFrameParser(clientAndServerCommandFactory());

	@Test
	public void parseFrameWithHeadersAndBody() {
		Frame actualFrame = underTest.parse(SEND_FRAME_WITH_HEADERS_AND_BODY);
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithNoBody() {
		Frame actualFrame = underTest.parse(STOMP_FRAME_NO_BODY);
		assertEquals(new Frame(ClientCommand.STOMP, new LinkedHashMap<String, String>() {{
			put("accept-version", "1.2");
			put("host", "localhost");
		}}, ""), actualFrame);
	}

	@Test
	public void parseFrameWithNoHeaders() {
		Frame actualFrame = underTest.parse(ERROR_FRAME_NO_HEADERS);
		assertEquals(new Frame(ServerCommand.ERROR, Collections.<String, String>emptyMap(), "The message:\n\n'hello mum!' was malformed"), actualFrame);
	}

	@Test
	public void parseFrameWithContentLengthHeaderAndNullsInBody() {
		Frame actualFrame = underTest.parse(ERROR_FRAME_WITH_NULL_IN_BODY);
		assertEquals(new Frame(ServerCommand.ERROR, Collections.singletonMap("content-length", "42"), "The message:\n\n'hello mum!\0' was malformed"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedColonInHeaders() {
		Frame actualFrame = underTest.parse("SEND\ndestination:/queue/foo\\cbar\n\nhello mum\0");
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo:bar"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedLineFeedInHeaders() {
		Frame actualFrame = underTest.parse("SEND\ndestination:/queue/foo\\rbar\n\nhello mum\0");
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo\nbar"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedCarriageReturnInHeaders() {
		Frame actualFrame = underTest.parse("SEND\ndestination:/queue/foo\\nbar\n\nhello mum\0");
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo\nbar"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedSlashInHeaders() {
		Frame actualFrame = underTest.parse("SEND\ndestination:/queue/foo\\\\bar\n\nhello mum\0");
		assertEquals(new Frame(ClientCommand.SEND, Collections.singletonMap("destination", "/queue/foo\\bar"), "hello mum"), actualFrame);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseEmptyFrame() {
		underTest.parse("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseNullFrame() {
		underTest.parse(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithNotEnoughLines() {
		underTest.parse("STOMP\n\0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithNoNewLineBetweenHeadersAndBody() {
		underTest.parse("STOMP\nhost:localhost\nhello mum\0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithUnrecognisedCommand() {
		underTest.parse("WANG\n\nhello mum\0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameThatIsNotTerminatedByNull() {
		underTest.parse("STOMP\n\n");
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
