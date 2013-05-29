package movint.mq.stomp.client.frame.builders;

import movint.mq.stomp.client.frame.Command;
import movint.mq.stomp.client.frame.Frame;
import movint.mq.stomp.client.frame.header.IdGenerator;

import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class DisconnectFrameBuilder {
	private static final String RECEIPT_HEADER = "receipt";

	private String receiptId;

	public Frame build() {
		return new Frame(Command.DISCONNECT, createHeaders(), null);
	}

	private LinkedHashMap<String, String> createHeaders() {
		return receiptId != null ? new LinkedHashMap<String, String>() {{
			put(RECEIPT_HEADER, receiptId);
		}} : null;
	}

	public DisconnectFrameBuilder withReceiptId(IdGenerator idGenerator) {
		this.receiptId = idGenerator.nextId();
		return this;
	}
}
