package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.CommandFactory;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameParser;
import movint.mq.stomp.client.frame.FrameSerializer;

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
	private final FrameParser frameParser = new FrameParser(new CommandFactory.ClientCommandFactory());
	private final Socket socket;

	public SocketConnection(String host, int port) throws IOException {
		this(host, port, SocketFactory.getDefault());
	}

	public SocketConnection(String host, int port, SocketFactory socketFactory) throws IOException {
		socket = socketFactory.createSocket(host, port);
	}

	@Override
	public synchronized Frame send(Frame frame) throws IOException {
		try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
			writer.print(frameSerializer.convertToWireFormat(frame));
		}
		String response = readResponse();
		return response.length() > 0 ? frameParser.parse(response) : null;
	}

	private String readResponse() throws IOException {
		StringBuilder response = new StringBuilder();
		if (!socket.isClosed()) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String inputLine;
				boolean firstLine = true;
				while ((inputLine = in.readLine()) != null) {
					if (firstLine) firstLine = false;
					else response.append("\n");
					response.append(inputLine);
				}
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
