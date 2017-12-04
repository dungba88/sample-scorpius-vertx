package org.joo.scorpius.support.graylog.msg;

import org.joo.scorpius.support.message.ExecutionContextStartMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnnotatedExecutionContextCreatedMessage extends AnnotatedGelfMessage {

    private static final long serialVersionUID = 3900326418162752886L;

    private ExecutionContextStartMessage msg;

    public AnnotatedExecutionContextCreatedMessage(ObjectMapper mapper, ExecutionContextStartMessage msg) {
        super();
        this.msg = msg;
        putField("executionContextId", msg.getId());
        putField("eventName", msg.getEventName());
        if (msg.getRequest() != null) {
            putField("traceId", msg.getRequest().getTraceId());
            try {
                putField("payload", mapper.writeValueAsString(msg.getRequest()));
            } catch (JsonProcessingException e) {
                putField("payloadEncodeException", e);
            }
        }
    }

    @Override
    public String getFormattedMessage() {
        return "Create event " + msg.getEventName() + " with id " + msg.getId();
    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }
}
