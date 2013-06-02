package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameSerializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public class SocketConnection implements Connection {
	private final FrameSerializer frameSerializer = new FrameSerializer();
	private final String host;
	private final int port;
	private Socket socket;

	public SocketConnection(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public synchronized void open() throws IOException {
		if (socket != null) {
			throw new IllegalStateException("Cannot open socket that is already connected");
		}
		socket = new Socket(host, port);
	}

	@Override
	public synchronized Frame send(Frame frame) throws IOException {
		try (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
			writer.print(frameSerializer.convertToWireFormat(frame));
		}
		return null;
	}

	@Override
	public synchronized void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
	}
}
