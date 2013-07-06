package movint.mq.stomp.client.frame;

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
public class FrameParserTest {

	public static final String STOMP_FRAME_NO_BODY = "STOMP\naccept-version:1.2\nhost:localhost\n\n\0";
	public static final String ERROR_FRAME_NO_HEADERS = "ERROR\n\nThe message:\n\n'hello mum!' was malformed\0";
	public static final String SEND_FRAME_WITH_HEADERS_AND_BODY = "SEND\ndestination:/queue/foo\n\nhello mum\0";

	@Test
	public void parseFrameWithHeadersAndBody() {
		Frame actualFrame = new FrameParser().parse(SEND_FRAME_WITH_HEADERS_AND_BODY);
		assertEquals(new Frame(Command.SEND, Collections.singletonMap("destination", "/queue/foo"), "hello mum"), actualFrame);
	}

	@Test
	public void parseFrameWithNoBody() {
		Frame actualFrame = new FrameParser().parse(STOMP_FRAME_NO_BODY);
		assertEquals(new Frame(Command.STOMP, new LinkedHashMap<String, String>() {{
			put("accept-version", "1.2");
			put("host", "localhost");
		}}, ""), actualFrame);
	}

	@Test
	public void parseFrameWithNoHeaders() {
		Frame actualFrame = new FrameParser().parse(ERROR_FRAME_NO_HEADERS);
		assertEquals(new Frame(Command.ERROR, Collections.<String, String>emptyMap(), "The message:\n\n'hello mum!' was malformed"), actualFrame);
	}

	@Test
	public void parseFrameWithEscapedCharactersInHeaders() {

	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseEmptyFrame() {
		new FrameParser().parse("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseNullFrame() {
		new FrameParser().parse(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithNotEnoughLines() {
		new FrameParser().parse("STOMP\n\0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithNoNewLineBetweenHeadersAndBody() {
		new FrameParser().parse("STOMP\nhost:localhost\nhello mum\0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameWithUnrecognisedCommand() {
		new FrameParser().parse("WANG\n\nhello mum\0");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotParseFrameThatIsNotTerminatedByNull() {
		new FrameParser().parse("STOMP\n\n");
	}
}
