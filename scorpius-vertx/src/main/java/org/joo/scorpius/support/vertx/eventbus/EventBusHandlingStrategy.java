package org.joo.scorpius.support.vertx.eventbus;

import java.io.IOException;

import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;
import org.msgpack.MessagePack;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;

public class EventBusHandlingStrategy implements TriggerHandlingStrategy {

    private static final String DEFAULT_ADDRESS = "scorpius";

    private EventBus eventBus;

    private String address;

    private MessageCodec<TriggerExecutionContext, TriggerExecutionContext> msgCodec;

    private boolean pubSub;

    public EventBusHandlingStrategy(EventBus eventBus) {
        this(eventBus, DEFAULT_ADDRESS);
    }

    public EventBusHandlingStrategy(EventBus eventBus, String address) {
        this(eventBus, address, false, false, new MessagePackExecutionContextCodec());
    }

    public EventBusHandlingStrategy(EventBus eventBus, boolean selfHandle) {
        this(eventBus, DEFAULT_ADDRESS, selfHandle);
    }

    public EventBusHandlingStrategy(EventBus eventBus, String address, boolean selfHandle) {
        this(eventBus, address, selfHandle, false, new MessagePackExecutionContextCodec());
    }

    public EventBusHandlingStrategy(EventBus eventBus, String address, boolean selfHandle, boolean pubSub,
            MessageCodec<TriggerExecutionContext, TriggerExecutionContext> msgCodec) {
        this.eventBus = eventBus;
        this.address = address;
        this.msgCodec = msgCodec;
        this.pubSub = pubSub;
        if (selfHandle)
            eventBus.<TriggerExecutionContext>consumer(address, this::onEvent);
    }

    @Override
    public void handle(TriggerExecutionContext context) {
        DeliveryOptions options = new DeliveryOptions().setCodecName(msgCodec.name());
        if (pubSub)
            eventBus.publish(address, context, options);
        else
            eventBus.send(address, context, options);
    }

    private void onEvent(Message<TriggerExecutionContext> event) {
        event.body().execute();
    }

    @Override
    public void start() {
        this.eventBus.registerCodec(new MessagePackExecutionContextCodec());
    }

    @Override
    public void shutdown() {
        eventBus.unregisterCodec(msgCodec.name());
    }
}

class MessagePackExecutionContextCodec implements MessageCodec<TriggerExecutionContext, TriggerExecutionContext> {

    private MessagePack msgpack = new MessagePack();

    @Override
    public void encodeToWire(Buffer buffer, TriggerExecutionContext s) {
        try {
            byte[] bytes = msgpack.write(s);
            buffer.appendBytes(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TriggerExecutionContext decodeFromWire(int pos, Buffer buffer) {
        int length = buffer.getInt(pos);
        byte[] bytes = buffer.getBytes(pos + 4, pos + 4 + length);
        try {
            return msgpack.read(bytes, TriggerExecutionContext.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TriggerExecutionContext transform(TriggerExecutionContext s) {
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