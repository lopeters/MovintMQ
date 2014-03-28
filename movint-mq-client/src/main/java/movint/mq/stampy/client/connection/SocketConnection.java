package movint.mq.stampy.client.connection;

import movint.mq.stomp.api.frame.*;

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
public class SocketConnection implements Connection, Closeable {
	public static final int DEFAULT_CONNECT_TIMEOUT = 5000;

	private final FrameSerializer frameSerializer = new FrameSerializer();
	private final StreamingFrameParser frameParser = new StreamingFrameParser(new CommandFactory.ServerCommandFactory());
	private final Socket socket;
	private ReceivedFrameHandler receivedFrameHandler;
	private OutputStreamWriter writer;
	private ExecutorService executor = newFixedThreadPool(2);

	public SocketConnection(String host, int port) throws IOException {
		this(host, port, DEFAULT_CONNECT_TIMEOUT, SocketFactory.getDefault());
	}

	public SocketConnection(String host, int port, int connectTimeout) throws IOException {
		this(host, port, connectTimeout, SocketFactory.getDefault());
	}

	public SocketConnection(String host, int port, int connectTimeout, SocketFactory socketFactory) throws IOException {
		socket = socketFactory.createSocket();
		socket.connect(new InetSocketAddress(host, port), connectTimeout);
		createSocketReadingThread();
		writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
	}

	private void createSocketReadingThread() {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
					while (!socket.isClosed()) {
						Frame receivedFrame = frameParser.parse(reader);
						if (receivedFrame != null) {
							System.out.println("Client received: " + receivedFrame);
							receivedFrameHandler.frameReceived(receivedFrame);
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public synchronized void send(Frame frame) throws IOException {
		writer.write(frameSerializer.convertToWireFormat(frame));
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
	public void addReceivedFrameHandler(ReceivedFrameHandler receivedFrameHandler) {
		this.receivedFrameHandler = receivedFrameHandler;
	}
}
