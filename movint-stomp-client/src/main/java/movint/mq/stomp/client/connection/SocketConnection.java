package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.CommandFactory;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameSerializer;
import movint.mq.stomp.client.frame.FrameStringParser;
import org.apache.commons.lang3.StringUtils;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public class SocketConnection implements Connection, Closeable {
	private final FrameSerializer frameSerializer = new FrameSerializer();
	private final FrameStringParser frameStringParser = new FrameStringParser(new CommandFactory.ServerCommandFactory());
	private final Socket socket;

	public SocketConnection(String host, int port) throws IOException {
		this(host, port, SocketFactory.getDefault());
	}

	public SocketConnection(String host, int port, SocketFactory socketFactory) throws IOException {
		socket = socketFactory.createSocket(host, port);
	}

	@Override
	public synchronized Frame send(Frame frame) throws IOException {
		String response;
		try (
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			writer.print(frameSerializer.convertToWireFormat(frame));
			writer.flush();
			socket.shutdownOutput();
			response = readResponse(in);
		}
		return StringUtils.isNotBlank(response) ? frameStringParser.parse(response) : null;
	}

	private String readResponse(BufferedReader in) throws IOException {
		StringBuilder response = new StringBuilder();
		if (!socket.isClosed()) {
			String inputLine;
			boolean firstLine = true;
			while ((inputLine = in.readLine()) != null) {
				if (firstLine) firstLine = false;
				else response.append("\n");
				response.append(inputLine);
			}
		}
		return response.toString();
	}

	@Override
	public synchronized void close() throws IOException {
		if (socket.isConnected()) {
			socket.close();
		}
	}
}
