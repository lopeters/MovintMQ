package movint.mq.client.transport;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:32
 */
public interface TransportFactory<T extends Transport> {
	String getHost();

	T createTransport();
}
