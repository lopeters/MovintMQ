package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameSerializer;

import javax.net.SocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public class SocketConnection implements Connection, AutoCloseable, Closeable {
	private final FrameSerializer frameSerializer = new FrameSerializer();
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
		return null;
	}

	@Override
	public synchronized void close() throws IOException {
		socket.close();
	}
}
