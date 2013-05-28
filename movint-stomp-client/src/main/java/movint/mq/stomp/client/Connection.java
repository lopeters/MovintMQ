package movint.mq.stomp.client;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 28/05/13
 * Time: 23:34
 */
public interface Connection {
	void send(Destination destination, Message message);

	void acknowledged(MessageId messageId);

	void acknowledged(MessageId messageId, TransactionId transactionId);

	void notAcknowledged(MessageId messageId);

	void notAcknowledged(MessageId messageId, TransactionId transactionId);

	/**
	 * @param destination STOMP destination to subscribe to
	 * @return a subscription ID that is unique to this connection
	 */
	SubscriptionId subscribe(Destination destination);

	void unsubscribe(SubscriptionId subscriptionId);

	void begin(TransactionId transactionId);

	void commit(TransactionId transactionId);

	void abort(TransactionId transactionId);

	void disconnect();
}
