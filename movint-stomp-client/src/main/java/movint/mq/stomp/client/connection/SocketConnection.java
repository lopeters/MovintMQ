package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.CommandFactory;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameSerializer;
import movint.mq.stomp.client.frame.FrameStreamParser;

import javax.net.SocketFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public class SocketConnection implements Connection, Closeable {
	private final FrameSerializer frameSerializer = new FrameSerializer();
	private final FrameStreamParser frameParser = new FrameStreamParser(new CommandFactory.ServerCommandFactory());
	private final Socket socket;

	public SocketConnection(String host, int port) throws IOException {
		this(host, port, SocketFactory.getDefault());
	}

	public SocketConnection(String host, int port, SocketFactory socketFactory) throws IOException {
		socket = socketFactory.createSocket(host, port);
	}

	@Override
	public synchronized Frame send(Frame frame) throws IOException {
		Frame response = null;
		try (
				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				InputStream in = socket.getInputStream()) {
			writer.print(frameSerializer.convertToWireFormat(frame));
			writer.flush();
			socket.shutdownOutput();
			if (!socket.isClosed()) {
				response = frameParser.parse(in);
			}
		}
		return response;
	}

	@Override
	public synchronized void close() throws IOException {
		if (socket.isConnected()) {
			socket.close();
		}
	}
}
