package movint.mq.api.stomp.frame.header;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 30/05/13
 * Time: 00:02
 */
public class SequentialIdGenerator implements IdGenerator {
	private final AtomicLong id = new AtomicLong(1);

	public String nextId() {
		return String.valueOf(id.getAndIncrement());
	}
}
