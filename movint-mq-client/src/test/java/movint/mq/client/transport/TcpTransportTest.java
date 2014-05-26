package movint.mq.client.transport;

import movint.mq.api.Destination;
import movint.mq.api.commands.ConnectCommand;
import movint.mq.api.commands.DisconnectCommand;
import movint.mq.api.commands.MessageCommand;
import movint.mq.api.stomp.StompWireFormat;
import movint.mq.api.stomp.frame.*;
import movint.mq.api.stomp.frame.builders.ConnectFrameBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.net.SocketFactory;
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
import static movint.mq.api.stomp.frame.ClientCommand.DISCONNECT;
import static movint.mq.api.stomp.frame.ClientCommand.SEND;
import static movint.mq.api.stomp.frame.ServerCommand.RECEIPT;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 30/05/13
 * Time: 00:52
 */
public class TcpTransportTest {
	private static final int SERVER_PORT = 2121;

	private volatile List<Frame> requests = new ArrayList<>();
	private TestServer testServer;
	private StompWireFormat wireFormat = new StompWireFormat();

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
		ConnectCommand connectCommand = new ConnectCommand("localhost");

		try (TcpTransport underTest = new TcpTransport("localhost", SERVER_PORT, 1000, SocketFactory.getDefault(), wireFormat)) {
			CommandListener commandListener = mock(CommandListener.class);
			underTest.setCommandListener(commandListener);
			underTest.send(connectCommand);
			Thread.sleep(100);
			assertThat(requests, hasItem(expectedConnectFrame()));
			System.out.println("This should be first");
			verifyZeroInteractions(commandListener);
			assertFalse(testServer.clientConnectionClosed());
		}
	}

	// TODO - fix me!
	@Test
	public void sendAFrameWithResponse() throws Exception {
		ConnectCommand connectCommand = new ConnectCommand("localhost");
		testServer.setResponse(new Frame(RECEIPT, singletonMap("header", "value"), "Hello son"));

		try (TcpTransport underTest = new TcpTransport("localhost", SERVER_PORT)) {
			CommandListener commandListener = mock(CommandListener.class);
			underTest.setCommandListener(commandListener);
			underTest.send(connectCommand);
			Thread.sleep(200);
			assertThat(requests, hasItem(expectedConnectFrame()));
			verify(commandListener).onCommand(new MessageCommand(null, singletonMap("header", "value"), "Hello son"));
		}
	}

	@Test
	public void closingTheConnectionClosesTheSocket() throws Exception {
		ConnectCommand connectCommand = new ConnectCommand("localhost");

		try (TcpTransport underTest = new TcpTransport("localhost", SERVER_PORT, 1000)) {
			underTest.send(connectCommand);
			underTest.close();
			Thread.sleep(200);
			assertTrue(testServer.clientConnectionClosed());
			assertThat(requests, hasItem(expectedConnectFrame()));
		}
	}

	@Test
	public void canSendMultipleFramesOnOneConnection() throws Exception {
		ConnectCommand connectCommand = new ConnectCommand("localhost");
		DisconnectCommand disconnectCommand = new DisconnectCommand();

		try (TcpTransport underTest = new TcpTransport("localhost", SERVER_PORT)) {
			underTest.send(connectCommand);
			Thread.sleep(200);
			assertThat(requests, hasItem(expectedConnectFrame()));
			underTest.send(disconnectCommand);
			Thread.sleep(200);
			assertThat(requests, hasItem(new Frame(DISCONNECT, singletonMap("receipt", "1"))));
		}
	}

	@Test
	public void frameIsEncodedAsUtf8() throws Exception {
		MessageCommand messageCommand = new MessageCommand(Destination.queue("queue"), null, "γεια σου μαμα");

		try (TcpTransport underTest = new TcpTransport("localhost", SERVER_PORT)) {
			underTest.send(messageCommand);
			Thread.sleep(200);
			assertThat(requests, hasItem(new Frame(SEND, singletonMap("destination", "/queue/queue"), "γεια σου μαμα")));
		}
	}

	private Frame expectedConnectFrame() {
		return new ConnectFrameBuilder("localhost", "1.0", "1.1", "1.2").build();
	}

	class TestServer {
		private final ServerSocket serverSocket;
		private final StreamingFrameParser frameParser = new StreamingFrameParser(new StompCommandFactory.ClientCommandFactory());
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
				e.printStackTrace();
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
