package movint.mq.api.stomp;

import movint.mq.api.Destination;
import movint.mq.api.commands.Command;
import movint.mq.api.commands.ConnectCommand;
import movint.mq.api.commands.DisconnectCommand;
import movint.mq.api.commands.MessageCommand;
import movint.mq.api.stomp.frame.*;
import movint.mq.api.stomp.frame.builders.ConnectFrameBuilder;
import movint.mq.api.stomp.frame.builders.DisconnectFrameBuilder;
import movint.mq.api.stomp.frame.builders.SendFrameBuilder;
import movint.mq.api.stomp.frame.header.IdGenerator;
import movint.mq.api.stomp.frame.header.SequentialIdGenerator;
import movint.mq.api.wireformat.WireFormat;

import java.io.BufferedReader;
import java.io.IOException;

public class StompWireFormat implements WireFormat {
	private static final String[] ACCEPTED_VERSIONS = new String[]{"1.0", "1.1", "1.2"};

	private final FrameParser<BufferedReader> frameParser;
	private final IdGenerator idGenerator;

	public StompWireFormat() {
		this(new StreamingFrameParser(new StompCommandFactory.ServerCommandFactory()), new SequentialIdGenerator());
	}

	public StompWireFormat(FrameParser<BufferedReader> frameParser, IdGenerator idGenerator) {
		this.frameParser = frameParser;
		this.idGenerator = idGenerator;
	}

	// TODO - use visitor pattern?
	@Override
	public String serialize(Command command) {
		if (command == null) {
			throw new IllegalArgumentException("Cannot serialize null command");
		}
		if (command instanceof ConnectCommand) {
			return new ConnectFrameBuilder(((ConnectCommand) command).getHost(), ACCEPTED_VERSIONS).build().serialize();
		}
		if (command instanceof DisconnectCommand) {
			return new DisconnectFrameBuilder().withReceiptId(idGenerator).build().serialize();
		}
		if (command instanceof MessageCommand) {
			MessageCommand messageCommand = (MessageCommand) command;
			return new SendFrameBuilder(messageCommand.getDestination(), messageCommand.getHeaders(), messageCommand.getBody()).build().serialize();
		}
		throw new UnsupportedOperationException("Cannot serialize unsupported command " + command.toString());
	}

	// TODO - push behaviour on to enum?
	@Override
	public Command deserialize(BufferedReader commandReader) throws IOException {
		Frame frame = frameParser.parse(commandReader);
		if (frame.getCommand() == ClientCommand.CONNECT || frame.getCommand() == ClientCommand.STOMP) {
			return new ConnectCommand(frame.getHeaders().get("host"));
		}
		if (frame.getCommand() == ClientCommand.SEND) {
			return new MessageCommand(Destination.parse(frame.getHeaders().get("destination")), frame.getHeaders(), frame.getBody());
		}
		if (frame.getCommand() == ClientCommand.DISCONNECT) {
			return new DisconnectCommand();
		}
		throw new UnsupportedOperationException("Unknown command: " + frame.getCommand());
	}
}
