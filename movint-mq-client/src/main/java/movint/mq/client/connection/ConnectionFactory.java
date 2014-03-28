package movint.mq.client.connection;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:32
 */
public interface ConnectionFactory<T extends Connection> {
	String getHost();

	T newConnection();
}
