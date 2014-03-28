package movint.mq.client.jms;

import javax.jms.JMSConsumer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MovintMQJMSConsumer implements JMSConsumer {
	private MessageListener messageListener = null;

	@Override
	public String getMessageSelector() {
		return null;
	}

	@Override
	public MessageListener getMessageListener() throws JMSRuntimeException {
		return messageListener;
	}

	@Override
	public void setMessageListener(MessageListener listener) throws JMSRuntimeException {
		this.messageListener = messageListener;
	}

	@Override
	public Message receive() {
		return null;
	}

	@Override
	public Message receive(long timeout) {
		return null;
	}

	@Override
	public Message receiveNoWait() {
		return null;
	}

	@Override
	public void close() {

	}

	@Override
	public <T> T receiveBody(Class<T> c) {
		return null;
	}

	@Override
	public <T> T receiveBody(Class<T> c, long timeout) {
		return null;
	}

	@Override
	public <T> T receiveBodyNoWait(Class<T> c) {
		return null;
	}
}
