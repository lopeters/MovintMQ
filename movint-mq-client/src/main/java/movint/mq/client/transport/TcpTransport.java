package movint.mq.client.transport;

import movint.mq.api.commands.Command;
import movint.mq.api.stomp.StompWireFormat;
import movint.mq.api.stomp.frame.CommandListener;
import movint.mq.api.wireformat.WireFormat;

import javax.net.SocketFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public class TcpTransport implements Transport, Closeable {
	public static final int DEFAULT_CONNECT_TIMEOUT = 5000;

	private final Socket socket;
	private final WireFormat wireFormat;

	private CommandListener commandListener;
	private OutputStreamWriter writer;
	private ExecutorService executor = newFixedThreadPool(2);

	public TcpTransport(String host, int port) throws IOException {
		this(host, port, DEFAULT_CONNECT_TIMEOUT, SocketFactory.getDefault(), new StompWireFormat());
	}

	public TcpTransport(String host, int port, int connectTimeout) throws IOException {
		this(host, port, connectTimeout, SocketFactory.getDefault(), new StompWireFormat());
	}

	public TcpTransport(String host, int port, int connectTimeout, SocketFactory socketFactory, StompWireFormat wireFormat) throws IOException {
		socket = socketFactory.createSocket();
		socket.connect(new InetSocketAddress(host, port), connectTimeout);
		createSocketReadingThread();
		writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
		this.wireFormat = wireFormat;
	}

	private void createSocketReadingThread() {
		executor.submit(() -> {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				while (!socket.isClosed()) {
					Command receivedCommand = wireFormat.deserialize(reader);
					if (receivedCommand != null) {
						System.out.println("Received command: " + receivedCommand);
						commandListener.onCommand(receivedCommand);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public synchronized void send(Command command) throws IOException {
		writer.write(wireFormat.serialize(command));
		writer.flush();
	}

	@Override
	public synchronized void close() throws IOException {
		if (socket.isConnected()) {
			executor.shutdown();
			socket.close();
		}
	}

	@Override
	public void setCommandListener(CommandListener commandListener) {
		this.commandListener = commandListener;
	}
}
