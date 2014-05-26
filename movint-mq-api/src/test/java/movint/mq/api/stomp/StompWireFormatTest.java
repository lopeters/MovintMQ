package movint.mq.api.stomp;

import movint.mq.api.Destination;
import movint.mq.api.commands.Command;
import movint.mq.api.commands.ConnectCommand;
import movint.mq.api.commands.DisconnectCommand;
import movint.mq.api.commands.MessageCommand;
import movint.mq.api.stomp.frame.Frame;
import movint.mq.api.stomp.frame.FrameParser;
import movint.mq.api.stomp.frame.StompCommand;
import movint.mq.api.stomp.frame.builders.DisconnectFrameBuilder;
import movint.mq.api.stomp.frame.builders.SendFrameBuilder;
import movint.mq.api.stomp.frame.header.IdGenerator;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;

import static movint.mq.api.stomp.frame.builders.ConnectFrameBuilder.connectFrameBuilder;
import static movint.mq.api.stomp.frame.builders.ConnectFrameBuilder.stompFrameBuilder;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StompWireFormatTest {

	private final FrameParser<BufferedReader> frameParser = mock(FrameParser.class);
	private final IdGenerator idGenerator = mock(IdGenerator.class);

	private final StompWireFormat underTest = new StompWireFormat(frameParser, idGenerator);

	@Test
	public void canDeserializeConnectCommand() throws IOException {
		Frame connectFrame = connectFrameBuilder("localhost", "1.0").build();
		BufferedReader bufferedReader = createReaderWithFrame(connectFrame);
		ConnectCommand deserializedCommand = (ConnectCommand) underTest.deserialize(bufferedReader);

		assertThat(deserializedCommand.getHost(), is("localhost"));
	}

	@Test
	public void canDeserializeStompCommand() throws IOException {
		Frame stompFrame = stompFrameBuilder("localhost", "1.0").build();
		BufferedReader bufferedReader = createReaderWithFrame(stompFrame);
		ConnectCommand deserializedCommand = (ConnectCommand) underTest.deserialize(bufferedReader);

		assertThat(deserializedCommand.getHost(), is("localhost"));
	}

	@Test
	public void canDeserializeSendCommand() throws IOException {
		Frame sendFrame = new SendFrameBuilder(Destination.queue("foo"), null, "Hello mum!").build();
		BufferedReader bufferedReader = createReaderWithFrame(sendFrame);
		MessageCommand deserializedCommand = (MessageCommand) underTest.deserialize(bufferedReader);

		assertThat(deserializedCommand.getDestination(), equalTo(Destination.queue("foo")));
		assertThat(deserializedCommand.getBody(), is("Hello mum!"));
	}

	@Test
	public void canDeserializeDisconnectCommand() throws IOException {
		Frame disconnectFrame = new DisconnectFrameBuilder().build();
		BufferedReader bufferedReader = createReaderWithFrame(disconnectFrame);
		Command deserializedCommand = underTest.deserialize(bufferedReader);

		assertThat(deserializedCommand, instanceOf(DisconnectCommand.class));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void throwsUnsupportedOperationExceptionForUnknownCommand() throws IOException {
		BufferedReader bufferedReader = createReaderWithFrame(new Frame(mock(StompCommand.class), null, null));

		underTest.deserialize(bufferedReader);
	}

	private BufferedReader createReaderWithFrame(Frame frame) throws IOException {
		BufferedReader bufferedReader = mock(BufferedReader.class);
		when(frameParser.parse(bufferedReader)).thenReturn(frame);
		return bufferedReader;
	}
}
