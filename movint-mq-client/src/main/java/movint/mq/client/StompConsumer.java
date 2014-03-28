package movint.mq.client;

import movint.mq.api.Message;

public interface StompConsumer {
	void onMessage(Message message);
}
