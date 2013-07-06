package movint.mq.stomp.client.frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 06/07/13
 * Time: 02:44
 */
public interface CommandFactory {
	Command createCommand(String commandName);
}
