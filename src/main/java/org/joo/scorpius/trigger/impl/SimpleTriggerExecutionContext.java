package org.joo.scorpius.trigger.impl;

import java.util.Optional;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerExecutionStatus;
import org.joo.scorpius.trigger.TriggerManager;

public class SimpleTriggerExecutionContext implements TriggerExecutionContext {

    private BaseRequest request;

    private ApplicationContext applicationContext;

    private String eventName;

    public SimpleTriggerExecutionContext(BaseRequest request, ApplicationContext applicationContext, String eventName) {
        this.eventName = eventName;
        this.request = request;
        this.applicationContext = applicationContext;
    }

    @Override
    public void pending() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void finish(BaseResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fail(TriggerExecutionException ex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseRequest getRequest() {
        return request;
    }

    @Override
    public TriggerExecutionStatus getStatus() {
        return null;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public TriggerManager getTriggerManager() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void attachTraceId(Optional<String> traceId) {
        request.attachTraceId(traceId);
    }

    @Override
    public boolean verifyTraceId() {
        return request.verifyTraceId();
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getTraceId() {
        return request.getTraceId();
    }

    @Override
    public Optional<String> fetchRawTraceId() {
        return request.fetchRawTraceId();
    }

    public Promise<BaseResponse, TriggerExecutionException> promise() {
        return null;
    }

    @Override
    public TriggerConfig getConfig() {
        return null;
    }
}
