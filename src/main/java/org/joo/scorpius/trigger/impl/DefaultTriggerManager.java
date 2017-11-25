package org.joo.scorpius.trigger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joo.libra.support.PredicateExecutionException;
import org.joo.promise4j.DoneCallback;
import org.joo.promise4j.FailCallback;
import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;
import org.joo.promise4j.impl.SimpleFailurePromise;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.builders.contracts.TriggerExecutionContextBuilderFactory;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.PeriodicTaskMessage;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.TriggerRegistration;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

public class DefaultTriggerManager extends AbstractTriggerEventDispatcher implements TriggerManager {

    private final static Logger logger = LogManager.getLogger(DefaultTriggerManager.class);

    private Map<String, List<TriggerConfig>> triggerConfigs;

    private @Getter ApplicationContext applicationContext;

    private @Getter @Setter TriggerHandlingStrategy handlingStrategy;

    private ScheduledExecutorService scheduledExecutors;

    private List<ScheduledFuture<?>> scheduledFutures;

    public DefaultTriggerManager(final ApplicationContext applicationContext) {
        this.triggerConfigs = new HashMap<>();
        this.applicationContext = applicationContext;
        this.handlingStrategy = applicationContext.getInstance(TriggerHandlingStrategyFactory.class).create();
        this.scheduledExecutors = Executors.newSingleThreadScheduledExecutor();
        this.scheduledFutures = new ArrayList<>();
    }

    @Override
    public BaseRequest decodeRequestForEvent(final String name, final String data) throws MalformedRequestException {
        if (name == null)
            throw new MalformedRequestException("Event name is null");

        if (!triggerConfigs.containsKey(name))
            return null;

        List<TriggerConfig> configs = triggerConfigs.get(name);
        if (configs.isEmpty())
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            return (BaseRequest) mapper.readValue(data, configs.get(0).getRequestClass());
        } catch (IOException e) {
            throw new MalformedRequestException(e);
        }
    }

    @Override
    public Promise<BaseResponse, TriggerExecutionException> fire(final String name, final BaseRequest data) {
        return fire(name, data, null, null);
    }

    @Override
    public Promise<BaseResponse, TriggerExecutionException> fire(final String name, final BaseRequest data,
            final DoneCallback<BaseResponse> doneCallback, final FailCallback<TriggerExecutionException> failCallback) {
        if (!triggerConfigs.containsKey(name))
            return resolveDefault(doneCallback);

        List<TriggerConfig> configs = triggerConfigs.get(name);
        if (configs.isEmpty())
            return resolveDefault(doneCallback);

        if (data != null && !data.verifyTraceId()) {
            TriggerExecutionException ex = new TriggerExecutionException("TraceId has not been attached");
            if (failCallback != null)
                failCallback.onFail(ex);
            return new SimpleFailurePromise<BaseResponse, TriggerExecutionException>(ex);
        }

        TriggerExecutionContext dummyExecutionContext = new SimpleTriggerExecutionContext(data, applicationContext,
                name);

        TriggerConfig config;
        try {
            config = findMatchingTrigger(configs, dummyExecutionContext);
        } catch (PredicateExecutionException e) {
            TriggerExecutionException ex = new TriggerExecutionException("Condition evaluation failed", e);
            if (failCallback != null)
                failCallback.onFail(ex);
            return new SimpleFailurePromise<BaseResponse, TriggerExecutionException>(ex);
        }

        if (config == null)
            return resolveDefault(doneCallback);

        TriggerExecutionContext executionContext = buildExecutionContext(name, data, config, doneCallback,
                failCallback);

        executionContext.pending();

        handlingStrategy.handle(executionContext);
        return executionContext.promise();
    }

    private TriggerConfig findMatchingTrigger(final List<TriggerConfig> configs,
            final TriggerExecutionContext dummyExecutionContext) throws PredicateExecutionException {
        for (TriggerConfig config : configs) {
            if (config.getCondition() == null || config.getCondition().satisfiedBy(dummyExecutionContext))
                return config;
        }
        return null;
    }

    private Promise<BaseResponse, TriggerExecutionException> resolveDefault(
            final DoneCallback<BaseResponse> doneCallback) {
        if (doneCallback != null)
            doneCallback.onDone(null);
        return new SimpleDonePromise<BaseResponse, TriggerExecutionException>(null);
    }

    private TriggerExecutionContext buildExecutionContext(final String name, final BaseRequest request,
            final TriggerConfig config, final DoneCallback<BaseResponse> doneCallback,
            final FailCallback<TriggerExecutionException> failCallback) {
        TriggerExecutionContextBuilder builder = applicationContext
                .getInstance(TriggerExecutionContextBuilderFactory.class).create();

        builder.setManager(this).setConfig(config).setRequest(request).setApplicationContext(applicationContext)
                .setDoneCallback(doneCallback).setFailCallback(failCallback).setEventName(name);

        return builder.build();
    }

    @Override
    public TriggerRegistration registerTrigger(final String name) {
        return registerTrigger(name, new TriggerConfig());
    }

    @Override
    public TriggerRegistration registerTrigger(final String name, final TriggerConfig triggerConfig) {
        if (!triggerConfigs.containsKey(name))
            triggerConfigs.put(name, new ArrayList<>());
        triggerConfigs.get(name).add(triggerConfig);
        return triggerConfig;
    }

    @Override
    public TriggerRegistration registerPeriodicEvent(final PeriodicTaskMessage msg) {
        return registerPeriodicEvent(msg, new TriggerConfig());
    }

    @Override
    public TriggerRegistration registerPeriodicEvent(final PeriodicTaskMessage msg, final TriggerConfig triggerConfig) {
        String name = UUID.randomUUID().toString();
        TriggerRegistration config = registerTrigger(name, triggerConfig);
        ScheduledFuture<?> future = scheduledExecutors.scheduleAtFixedRate(() -> {
            fire(name, msg.getRequest());
        }, msg.getDelay(), msg.getPeriod(), TimeUnit.MILLISECONDS);
        scheduledFutures.add(future);
        return config;
    }

    @Override
    public void shutdown() {
        for (ScheduledFuture<?> future : scheduledFutures) {
            future.cancel(true);
        }
        scheduledExecutors.shutdown();
        try {
            handlingStrategy.close();
        } catch (Exception e) {
            logger.warn("Exception occurred when closing handling strategy", e);
        }
    }

    public List<ScheduledFuture<?>> getScheduledFutures() {
        return scheduledFutures;
    }
}
