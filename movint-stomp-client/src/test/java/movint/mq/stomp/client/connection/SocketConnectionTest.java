package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.ClientCommand;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameSerializer;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 30/05/13
 * Time: 00:52
 */
public class SocketConnectionTest {
	private static final int SERVER_PORT = 2121;

	@Test
	public void sendAFrameWithNoResponse() throws Exception {
		Frame frame = new Frame(ClientCommand.STOMP, null, "Hello Mum!");
		Future<String> future = startServerSocket();

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT)) {
			underTest.send(frame);
			assertEquals(new FrameSerializer().convertToWireFormat(frame), future.get(1000, TimeUnit.MILLISECONDS));
		}
	}

	@Test
	public void sendAFrameWithResponse() throws Exception {
		fail("Implement me");
	}

	@Test
	public void frameIsEncodedAsUtf8() {
		fail("Implement me");
	}

	@Test
	public void closeTheConnection() {
		fail("Implement me");
	}

	public Future<String> startServerSocket() throws IOException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		return executor.submit(
				new Callable<String>() {
					@Override
					public String call() {
						try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
							try (Socket clientSocket = serverSocket.accept()) {
								try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
									String inputLine;
									StringBuilder sb = new StringBuilder();
									boolean firstLine = true;
									while ((inputLine = in.readLine()) != null) {
										if (firstLine) {
											firstLine = false;
										} else {
											sb.append("\n");
										}
										sb.append(inputLine);
									}
									return sb.toString();
								}
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});
	}
}
