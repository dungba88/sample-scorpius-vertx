package org.joo.scorpius.trigger.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.joo.promise4j.impl.FailSafePromise;
import org.joo.promise4j.impl.SimpleDonePromise;
import org.joo.promise4j.impl.SimpleFailurePromise;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.builders.contracts.TriggerExecutionContextBuilderFactory;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.builders.contracts.TriggerRepositoryFactory;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.PeriodicTaskMessage;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.TriggerRegistration;
import org.joo.scorpius.trigger.TriggerRepository;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import net.jodah.failsafe.SyncFailsafe;

public class DefaultTriggerManager extends AbstractTriggerEventDispatcher implements TriggerManager {

    private static final Logger logger = LogManager.getLogger(DefaultTriggerManager.class);

    private @Getter ApplicationContext applicationContext;

    private @Getter @Setter TriggerRepository triggerRepository;

    private @Getter @Setter TriggerHandlingStrategy handlingStrategy;

    private ScheduledExecutorService scheduledExecutors;

    private @Getter List<ScheduledFuture<?>> scheduledFutures;

    public DefaultTriggerManager(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.triggerRepository = applicationContext.getInstance(TriggerRepositoryFactory.class).create();
        this.handlingStrategy = applicationContext.getInstance(TriggerHandlingStrategyFactory.class).create();
        this.scheduledFutures = new ArrayList<>();
        this.scheduledExecutors = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void start() {
        triggerRepository.start();
        handlingStrategy.start();
    }

    @Override
    public BaseRequest decodeRequestForEvent(final String name, final String data) throws MalformedRequestException {
        if (data == null || data.isEmpty())
            return null;

        if (name == null)
            throw new MalformedRequestException("Event name is null");

        List<TriggerConfig> configs = triggerRepository.getTriggerConfigs(name);
        if (configs == null || configs.isEmpty())
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
        return fireWithFailSafe(name, data, doneCallback, failCallback, null);
    }

    @Override
    public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data,
            SyncFailsafe<Object> failSafe) {
        return fireWithFailSafe(name, data, null, null, failSafe);
    }

    private Promise<BaseResponse, TriggerExecutionException> fireWithFailSafe(final String name, final BaseRequest data,
            final DoneCallback<BaseResponse> doneCallback, final FailCallback<TriggerExecutionException> failCallback,
            SyncFailsafe<Object> failSafe) {
        if (data != null && !data.verifyTraceId()) {
            TriggerExecutionException ex = new TriggerExecutionException("TraceId has not been attached");
            return rejectDefault(failCallback, ex);
        }

        List<TriggerConfig> configs = triggerRepository.getTriggerConfigs(name);

        if (configs == null)
            return resolveDefault(doneCallback);

        TriggerExecutionContext dummyContext = new SimpleTriggerExecutionContext(data, applicationContext, name);

        TriggerConfig config;
        try {
            config = findMatchingTrigger(configs, dummyContext);
        } catch (PredicateExecutionException e) {
            TriggerExecutionException ex = new TriggerExecutionException("Condition evaluation failed", e);
            return rejectDefault(failCallback, ex);
        }

        if (config == null)
            return resolveDefault(doneCallback);

        failSafe = (failSafe != null) ? failSafe : config.getFailSafe();
        if (failSafe == null)
            return handleTrigger(name, data, doneCallback, failCallback, config);
        return FailSafePromise.fromPromise(() -> {
            return handleTrigger(name, data, doneCallback, failCallback, config);
        }, failSafe.with(scheduledExecutors));
    }

    private Promise<BaseResponse, TriggerExecutionException> handleTrigger(final String name, final BaseRequest data,
            final DoneCallback<BaseResponse> doneCallback, final FailCallback<TriggerExecutionException> failCallback,
            final TriggerConfig config) {
        TriggerExecutionContext executionContext = buildExecutionContext(name, data, config, doneCallback,
                failCallback);
        executionContext.pending();
        TriggerHandlingStrategy effectiveStrategy = config.getStrategy();
        if (effectiveStrategy == null)
            effectiveStrategy = handlingStrategy;
        effectiveStrategy.handle(executionContext);
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

    private Promise<BaseResponse, TriggerExecutionException> rejectDefault(
            final FailCallback<TriggerExecutionException> failCallback, final TriggerExecutionException ex) {
        if (failCallback != null)
            failCallback.onFail(ex);
        return new SimpleFailurePromise<BaseResponse, TriggerExecutionException>(ex);
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
    public TriggerRegistration registerTrigger(String name) {
        return registerTrigger(name, new TriggerConfig());
    }

    @Override
    public TriggerRegistration registerTrigger(final String name, final TriggerConfig triggerConfig) {
        return triggerRepository.registerTrigger(name, triggerConfig);
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
        clearEventHandlers();
        for (ScheduledFuture<?> future : scheduledFutures) {
            future.cancel(true);
        }
        scheduledExecutors.shutdown();
        triggerRepository.shutdown();
        handlingStrategy.shutdown();
    }
}
