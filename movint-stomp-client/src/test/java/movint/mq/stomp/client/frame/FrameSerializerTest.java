package movint.mq.stomp.client.frame;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 02/06/13
 * Time: 00:17
 */
public class FrameSerializerTest {
	@Test
	public void serialisesFrameWithMultipleHeaders() {
		Frame frame = new Frame(ClientCommand.STOMP, new LinkedHashMap<String, String>() {{
			put("key1", "value1");
			put("key2", "value2");
		}}, "Hello mum!");
		assertEquals("STOMP\nkey1:value1\nkey2:value2\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void serialisesFrameWithNoBody() {
		Frame frame = new Frame(ClientCommand.STOMP, Collections.singletonMap("key1", "value1"), "Hello mum!");
		assertEquals("STOMP\nkey1:value1\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void escapesColonInHeader() {
		Frame frame = new Frame(ClientCommand.STOMP, Collections.singletonMap("key:", "value::"), "Hello mum!");
		assertEquals("STOMP\nkey\\c:value\\c\\c\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void escapesLineFeedInHeader() {
		Frame frame = new Frame(ClientCommand.STOMP, Collections.singletonMap("key\n", "value\n\n"), "Hello mum!");
		assertEquals("STOMP\nkey\\n:value\\n\\n\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void escapesCarriageReturnInHeader() {
		Frame frame = new Frame(ClientCommand.STOMP, Collections.singletonMap("key\r", "value\r\r"), "Hello mum!");
		assertEquals("STOMP\nkey\\r:value\\r\\r\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void escapesSlashInHeader() {
		Frame frame = new Frame(ClientCommand.STOMP, Collections.singletonMap("key\\", "value\\\\"), "Hello mum!");
		assertEquals("STOMP\nkey\\\\:value\\\\\\\\\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void escapesEverythingInHeaders() {
		Frame frame = new Frame(ClientCommand.STOMP, Collections.singletonMap(":\\\r\n", ":\\\r\n"), "Hello mum!");
		assertEquals("STOMP\n\\c\\\\\\r\\n:\\c\\\\\\r\\n\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void serialisesFrameWithNullHeaders() {
		Frame frame = new Frame(ClientCommand.STOMP, null, "Hello mum!");
		assertEquals("STOMP\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}

	@Test
	public void serialisesFrameWithEmptyHeaders() {
		Frame frame = new Frame(ClientCommand.STOMP, new HashMap<String, String>(), "Hello mum!");
		assertEquals("STOMP\n\nHello mum!\0", new FrameSerializer().convertToWireFormat(frame));
	}
}
