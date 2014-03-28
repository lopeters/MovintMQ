package movint.mq.stampy.client;

import movint.mq.stomp.api.Message;

public interface StompConsumer {
	void onMessage(Message message);
}
