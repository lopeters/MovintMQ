package movint.mq.api.stomp.frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 23:01
 */
public enum ClientCommand implements StompCommand {
	CONNECT, STOMP, SEND, ACK, NACK, SUBSCRIBE, UNSUBSCRIBE, BEGIN, COMMIT, ABORT, DISCONNECT
}
