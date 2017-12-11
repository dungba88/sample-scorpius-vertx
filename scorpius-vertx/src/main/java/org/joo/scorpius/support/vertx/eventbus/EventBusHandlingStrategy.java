package org.joo.scorpius.support.vertx.eventbus;

import java.io.IOException;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.ExecutionContextMessage;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;
import org.msgpack.MessagePack;

import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;

public class EventBusHandlingStrategy implements TriggerHandlingStrategy {

	private static final String DEFAULT_ADDRESS = "scorpius";

	private EventBus eventBus;

	private String address;

	private MessageCodec<ExecutionContextMessage, ExecutionContextMessage> msgCodec;

	private boolean pubSub;

	public EventBusHandlingStrategy(EventBus eventBus) {
		this(eventBus, DEFAULT_ADDRESS);
	}

	public EventBusHandlingStrategy(EventBus eventBus, String address) {
		this(eventBus, address, false, new MessagePackExecutionContextCodec());
	}

	public EventBusHandlingStrategy(EventBus eventBus, boolean pubSub) {
		this(eventBus, DEFAULT_ADDRESS, pubSub);
	}

	public EventBusHandlingStrategy(EventBus eventBus, String address, boolean pubSub) {
		this(eventBus, address, pubSub, new MessagePackExecutionContextCodec());
	}

	public EventBusHandlingStrategy(EventBus eventBus, String address, boolean pubSub,
			MessageCodec<ExecutionContextMessage, ExecutionContextMessage> msgCodec) {
		this.eventBus = eventBus;
		this.address = address;
		this.msgCodec = msgCodec;
		this.pubSub = pubSub;
	}

	@Override
	public void handle(TriggerExecutionContext context) {
		ExecutionContextMessage message = context.toMessage();
		if (pubSub)
			eventBus.publish(address, message);
		else
			eventBus.<ExecutionContextMessage>send(address, message, result -> handleReply(result, context));
	}

	private void handleReply(AsyncResult<Message<ExecutionContextMessage>> result, TriggerExecutionContext context) {
		if (result.failed()) {
			context.fail(new TriggerExecutionException(result.cause()));
		} else {
			BaseResponse response = (BaseResponse) result.result().body().getData();
			context.finish(response);
		}
	}

	@Override
	public void start() {
		eventBus.unregisterDefaultCodec(ExecutionContextMessage.class);
		eventBus.registerDefaultCodec(ExecutionContextMessage.class, msgCodec);
	}

	@Override
	public void shutdown() {
		eventBus.unregisterDefaultCodec(ExecutionContextMessage.class);
	}
}

class MessagePackExecutionContextCodec implements MessageCodec<ExecutionContextMessage, ExecutionContextMessage> {

	private MessagePack msgpack = new MessagePack();

	@Override
	public void encodeToWire(Buffer buffer, ExecutionContextMessage s) {
		try {
			byte[] bytes = msgpack.write(s);
			buffer.appendBytes(bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ExecutionContextMessage decodeFromWire(int pos, Buffer buffer) {
		int length = buffer.getInt(pos);
		byte[] bytes = buffer.getBytes(pos + 4, pos + 4 + length);
		try {
			return msgpack.read(bytes, ExecutionContextMessage.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ExecutionContextMessage transform(ExecutionContextMessage s) {
		return s;
	}

	@Override
	public String name() {
		return "executionContext";
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
}