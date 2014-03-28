package movint.mq.api.stomp.frame;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 10/07/13
 * Time: 02:13
 */
public interface FrameParser<T> {
	Frame parse(T toParse) throws IOException;
}
