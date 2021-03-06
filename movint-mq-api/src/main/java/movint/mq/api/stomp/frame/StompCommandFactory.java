package movint.mq.api.stomp.frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 06/07/13
 * Time: 02:44
 */
public interface StompCommandFactory {
	StompCommand createCommand(String commandName);

	public class ClientCommandFactory implements StompCommandFactory {
		@Override
		public StompCommand createCommand(String commandName) {
			return ClientCommand.valueOf(commandName);
		}
	}

	public class ServerCommandFactory implements StompCommandFactory {
		@Override
		public StompCommand createCommand(String commandName) {
			return ServerCommand.valueOf(commandName);
		}
	}
}
