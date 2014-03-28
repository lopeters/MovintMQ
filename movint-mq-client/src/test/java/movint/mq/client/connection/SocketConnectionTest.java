package movint.mq.client.connection;

import movint.mq.api.frame.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonMap;
import static movint.mq.api.frame.ClientCommand.DISCONNECT;
import static movint.mq.api.frame.ClientCommand.STOMP;
import static movint.mq.api.frame.ServerCommand.RECEIPT;
import static org.hamcrest.CoreMatchers.hasItem;
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

	private volatile List<Frame> requests = new ArrayList<>();
	private TestServer testServer;

	@Before
	public void startServer() throws IOException {
		testServer = new TestServer();
		new Thread(new Runnable() {
			@Override
			public void run() {
				testServer.listen();
			}
		}).start();
	}

	@After
	public void stopServer() throws IOException {
		testServer.stop();
	}

	@Test
	public void sendAFrameWithNoResponse() throws Exception {
		Frame outgoingMessage = new Frame(STOMP, null, "Hello Mum!");

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT, 1000)) {
			ReceivedFrameHandler receivedFrameHandler = mock(ReceivedFrameHandler.class);
			underTest.addReceivedFrameHandler(receivedFrameHandler);
			underTest.send(outgoingMessage);
			Thread.sleep(200);
			assertThat(requests, hasItem(outgoingMessage));
			verifyZeroInteractions(receivedFrameHandler);
			assertFalse(testServer.clientConnectionClosed());
		}
	}

	@Test
	public void sendAFrameWithResponse() throws Exception {
		Frame outgoingMessage = new Frame(STOMP, null, "Hello Mum!");
		Frame expectedResponse = new Frame(RECEIPT, singletonMap("header", "value"), "Hello son");
		testServer.setResponse(expectedResponse);

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT)) {
			ReceivedFrameHandler receivedFrameHandler = mock(ReceivedFrameHandler.class);
			underTest.addReceivedFrameHandler(receivedFrameHandler);
			underTest.send(outgoingMessage);
			Thread.sleep(200);
			assertThat(requests, hasItem(outgoingMessage));
			verify(receivedFrameHandler).frameReceived(expectedResponse);
		}
	}

	@Test
	public void closingTheConnectionClosesTheSocket2() throws Exception {
		Frame outgoingMessage = new Frame(STOMP, null, "Hello Mum!");

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT, 1000)) {
			underTest.send(outgoingMessage);
			underTest.close();
			Thread.sleep(200);
			assertTrue(testServer.clientConnectionClosed());
			assertThat(requests, hasItem(outgoingMessage));
		}
	}

	@Test
	public void canSendMultipleFramesOnOneConnection() throws Exception {
		Frame firstOutgoingMessage = new Frame(STOMP, null, "Hello Mum!");
		Frame secondOutgoingMessage = new Frame(DISCONNECT, null, "");

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT)) {
			underTest.send(firstOutgoingMessage);
			Thread.sleep(200);
			assertThat(requests, hasItem(firstOutgoingMessage));
			underTest.send(secondOutgoingMessage);
			Thread.sleep(200);
			assertThat(requests, hasItem(secondOutgoingMessage));
		}
	}

	@Test
	public void frameIsEncodedAsUtf8() throws Exception {
		Frame outgoingMessage = new Frame(STOMP, null, "γεια σου μαμα");

		try (SocketConnection underTest = new SocketConnection("localhost", SERVER_PORT)) {
			underTest.send(outgoingMessage);
			Thread.sleep(200);
			assertThat(requests, hasItem(outgoingMessage));
		}
	}

	class TestServer {
		private final ServerSocket serverSocket;
		private final StreamingFrameParser frameParser = new StreamingFrameParser(new CommandFactory.ClientCommandFactory());
		private Frame response;
		private boolean socketClosed = false;

		public TestServer() throws IOException {
			serverSocket = new ServerSocket(SERVER_PORT);
		}

		public void setResponse(Frame response) {
			this.response = response;
		}

		public void listen() {
			try (Socket clientSocket = serverSocket.accept()) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
				     PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
					Frame received;
					while ((received = frameParser.parse(reader)) != null) {
						System.out.println("Server received: " + received);
						requests.add(received);
						writer.print(response != null ? new FrameSerializer().convertToWireFormat(response) : System.lineSeparator());
						writer.flush();
					}
				}
			} catch (SocketException e) {
				socketClosed = true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void stop() throws IOException {
			serverSocket.close();
		}

		public boolean clientConnectionClosed() {
			return socketClosed;
		}
	}
}
