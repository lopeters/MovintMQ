package movint.mq.api.wireformat;

import movint.mq.api.commands.Command;

import java.io.BufferedReader;
import java.io.IOException;

public interface WireFormat {
	String serialize(Command command);

	Command deserialize(BufferedReader commandReader) throws IOException;
}
