package movint.mq.stomp.client;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:32
 */
public interface ConnectionFactory<T extends Connection> {
	T connect();
}
