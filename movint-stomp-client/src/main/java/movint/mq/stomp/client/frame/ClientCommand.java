package movint.mq.stomp.client.frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 23:01
 */
public enum ClientCommand implements Command {
	CONNECT, STOMP, SEND, ACK, NACK, SUBSCRIBE, UNSUBSCRIBE, BEGIN, COMMIT, ABORT, DISCONNECT
}
