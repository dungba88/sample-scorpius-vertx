package org.joo.scorpius.trigger.impl;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joo.promise4j.Deferred;
import org.joo.promise4j.Promise;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.ExecutionContextExceptionMessage;
import org.joo.scorpius.support.message.ExecutionContextFinishMessage;
import org.joo.scorpius.support.message.ExecutionContextStartMessage;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerEvent;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerExecutionStatus;
import org.joo.scorpius.trigger.TriggerManager;

import lombok.Getter;

@Getter
public class DefaultTriggerExecutionContext implements TriggerExecutionContext {

    private static final Logger logger = LogManager.getLogger(DefaultTriggerExecutionContext.class);

    private String id;

    private String eventName;

    private TriggerConfig config;

    private BaseRequest request;

    private ApplicationContext applicationContext;

    private TriggerExecutionStatus status;

    private Deferred<BaseResponse, TriggerExecutionException> deferred;

    private TriggerManager triggerManager;

    public DefaultTriggerExecutionContext(final TriggerManager manager, final TriggerConfig config, final BaseRequest request,
            final ApplicationContext applicationContext, final Deferred<BaseResponse, TriggerExecutionException> deferred,
            final String id, final String eventName) {
        this.id = id;
        this.eventName = eventName;
        this.triggerManager = manager;
        this.config = config;
        this.request = request;
        this.applicationContext = applicationContext;
        this.status = TriggerExecutionStatus.CREATED;
        this.deferred = deferred;
    }

    @Override
    public void pending() {
        if (triggerManager.isEventEnabled(TriggerEvent.CREATED))
            triggerManager.notifyEvent(TriggerEvent.CREATED, new ExecutionContextStartMessage(id, eventName, request));
        status = TriggerExecutionStatus.PENDING;
    }

    @Override
    public void execute() {
        if (status == TriggerExecutionStatus.EXECUTING || status == TriggerExecutionStatus.FINISHED) {
            throw new IllegalStateException("Trigger is already running or finished");
        }
        if (config.getTrigger() == null)
            return;

        if (triggerManager.isEventEnabled(TriggerEvent.START))
            triggerManager.notifyEvent(TriggerEvent.START, new ExecutionContextStartMessage(id, eventName, request));

        try {
            config.getTrigger().execute(this);
        } catch (TriggerExecutionException e) {
            fail(e);
        } catch (Exception e) {
            fail(new TriggerExecutionException(e));
        }
    }

    @Override
    public void finish(final BaseResponse response) {
        if (status == TriggerExecutionStatus.FINISHED)
            throw new IllegalStateException("Trigger is already finished");

        deferred.resolve(response);

        status = TriggerExecutionStatus.FINISHED;

        if (triggerManager.isEventEnabled(TriggerEvent.FINISH))
            triggerManager.notifyEvent(TriggerEvent.FINISH,
                    new ExecutionContextFinishMessage(id, eventName, request, response));
    }

    @Override
    public void fail(final TriggerExecutionException ex) {
        logException(ex);

        if (status == TriggerExecutionStatus.FINISHED)
            return;

        deferred.reject(ex);

        status = TriggerExecutionStatus.FINISHED;
    }

    public void logException(final TriggerExecutionException ex) {
        if (logger.isErrorEnabled()) {
            logger.error("Exception occured while executing trigger with event name {}", eventName, ex);
        }
        if (triggerManager.isEventEnabled(TriggerEvent.EXCEPTION))
            triggerManager.notifyEvent(TriggerEvent.EXCEPTION,
                    new ExecutionContextExceptionMessage(id, eventName, request, ex));
    }

    @Override
    public Promise<BaseResponse, TriggerExecutionException> promise() {
        return deferred.promise();
    }

    @Override
    public void attachTraceId(final Optional<String> traceId) {
        request.attachTraceId(traceId);
    }

    @Override
    public boolean verifyTraceId() {
        return request.verifyTraceId();
    }

    @Override
    public String getTraceId() {
        return request.getTraceId();
    }

    @Override
    public Optional<String> fetchRawTraceId() {
        return request.fetchRawTraceId();
    }
}
