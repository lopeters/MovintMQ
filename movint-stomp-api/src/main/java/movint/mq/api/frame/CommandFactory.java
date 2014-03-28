package movint.mq.api.frame;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 06/07/13
 * Time: 02:44
 */
public interface CommandFactory {
	Command createCommand(String commandName);

	public class ClientCommandFactory implements CommandFactory {
		@Override
		public Command createCommand(String commandName) {
			return ClientCommand.valueOf(commandName);
		}
	}

	public class ServerCommandFactory implements CommandFactory {
		@Override
		public Command createCommand(String commandName) {
			return ServerCommand.valueOf(commandName);
		}
	}
}
