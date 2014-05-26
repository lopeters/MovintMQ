package movint.mq.api.stomp.frame.builders;

import movint.mq.api.stomp.frame.ClientCommand;
import movint.mq.api.stomp.frame.Frame;
import movint.mq.api.stomp.frame.header.IdGenerator;

import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Luke
 * Date: 29/05/13
 * Time: 22:51
 */
public class DisconnectFrameBuilder implements FrameBuilder {
	private static final String RECEIPT_HEADER = "receipt";

	private String receiptId;

	public Frame build() {
		return new Frame(ClientCommand.DISCONNECT, createHeaders(), null);
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
