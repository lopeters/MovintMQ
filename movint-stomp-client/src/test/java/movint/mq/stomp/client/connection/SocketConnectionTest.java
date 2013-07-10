package movint.mq.stomp.client.connection;

import movint.mq.stomp.client.frame.CommandFactory;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.FrameSerializer;
import movint.mq.stomp.client.frame.StreamingFrameParser;
import org.junit.Test;

import javax.net.SocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static movint.mq.stomp.client.frame.ClientCommand.STOMP;
import static movint.mq.stomp.client.frame.ServerCommand.RECEIPT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
		Frame outgoingMessage = new Frame(STOMP, null, "Hello Mum!");
		Future<Frame> messageFuture = startServerWithNoResponse();

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT)) {
			Frame response = underTest.send(outgoingMessage);
			assertEquals(outgoingMessage, messageFuture.get(1000, MILLISECONDS));
			assertNull(response);
		}
	}

	@Test
	public void sendAFrameWithResponse() throws Exception {
		Frame outgoingMessage = new Frame(STOMP, null, "Hello Mum!");
		Frame expectedResponse = new Frame(RECEIPT, singletonMap("header", "value"), "Hello son");
		Future<Frame> messageFuture = startServerThatWillRespondWith(expectedResponse);

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT)) {
			Frame actualResponse = underTest.send(outgoingMessage);
			assertEquals(outgoingMessage, messageFuture.get(1000, MILLISECONDS));
			assertEquals(expectedResponse, actualResponse);
		}
	}

	@Test
	public void frameIsEncodedAsUtf8() {
		fail("Implement me");
	}

	@Test
	public void closeTheConnection() throws IOException {
		SocketFactory socketFactory = mock(SocketFactory.class);
		Socket socket = mock(Socket.class);
		when(socketFactory.createSocket("localhost", SERVER_PORT)).thenReturn(socket);
		when(socket.isConnected()).thenReturn(true);

		new SocketConnection("localhost", SERVER_PORT, socketFactory).close();
		verify(socket).close();
	}

	public Future<Frame> startServerWithNoResponse() throws IOException {
		return startServerThatWillRespondWith(null);
	}

	public Future<Frame> startServerThatWillRespondWith(final Frame response) throws IOException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		return executor.submit(
				new Callable<Frame>() {
					@Override
					public Frame call() {
						try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
							try (Socket clientSocket = serverSocket.accept()) {
								try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
								     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
									Frame received = new StreamingFrameParser(new CommandFactory.ClientCommandFactory()).parse(reader);
									if (response != null) {
										writer.print(new FrameSerializer().convertToWireFormat(response));
										writer.flush();
									}
									return received;
								}
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});
	}
}
