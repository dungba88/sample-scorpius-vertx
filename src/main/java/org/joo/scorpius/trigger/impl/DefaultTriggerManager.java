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
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.builders.contracts.TriggerExecutionContextBuilderFactory;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.deferred.DoneCallback;
import org.joo.scorpius.support.deferred.FailCallback;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.deferred.SimpleDonePromise;
import org.joo.scorpius.support.deferred.SimpleFailurePromise;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.PeriodicTaskMessage;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.TriggerRegistration;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultTriggerManager extends AbstractTriggerEventDispatcher implements TriggerManager {
	
	private final static Logger logger = LogManager.getLogger(DefaultTriggerManager.class);
	
	private Map<String, List<TriggerConfig>> triggerConfigs;
	
	private ApplicationContext applicationContext;
	
	private TriggerHandlingStrategy handlingStrategy;
	
	private ScheduledExecutorService scheduledExecutors;
	
	private List<ScheduledFuture<?>> scheduledFutures;
	
	public DefaultTriggerManager(ApplicationContext applicationContext) {
		this.triggerConfigs = new HashMap<>();
		this.applicationContext = applicationContext;
		this.handlingStrategy = applicationContext.getInstance(TriggerHandlingStrategyFactory.class).create();
		this.scheduledExecutors = Executors.newSingleThreadScheduledExecutor();
		this.scheduledFutures = new ArrayList<>();
	}
	
	@Override
	public BaseRequest decodeRequestForEvent(String name, String data) throws MalformedRequestException {
		if (name == null)
			throw new MalformedRequestException("Event name is null");
		
		if (!triggerConfigs.containsKey(name))
			return null;

		List<TriggerConfig> configs = triggerConfigs.get(name);
		if (configs.isEmpty()) return null;

		ObjectMapper mapper = new ObjectMapper();
		try {
			return (BaseRequest) mapper.readValue(data, configs.get(0).getRequestClass());
		} catch (IOException e) {
			throw new MalformedRequestException(e);
		}
	}

	@Override
	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data) {
		return fire(name, data, null, null);
	}
	
	@Override
	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data, 
																 DoneCallback<BaseResponse> doneCallback, 
																 FailCallback<TriggerExecutionException> failCallback) {
		if (!triggerConfigs.containsKey(name)) return resolveDefault(doneCallback);

		List<TriggerConfig> configs = triggerConfigs.get(name);
		if (configs.isEmpty()) return resolveDefault(doneCallback);
		
		if (!data.verifyTraceId()) {
			TriggerExecutionException ex = new TriggerExecutionException("TraceId has not been attached");
			if (failCallback != null)
				failCallback.onFail(ex);
			return new SimpleFailurePromise<BaseResponse, TriggerExecutionException>(ex);
		}
		
		TriggerExecutionContext dummyExecutionContext = new SimpleTriggerExecutionContext(data, applicationContext, name);
				
		TriggerConfig config = findMatchingTrigger(configs, dummyExecutionContext);

		if (config == null) return resolveDefault(doneCallback);

		TriggerExecutionContext executionContext = buildExecutionContext(name, data, configs.get(0), doneCallback, failCallback);
		
		
		handlingStrategy.handle(executionContext);
		return executionContext.promise();
	}
	
	private TriggerConfig findMatchingTrigger(List<TriggerConfig> configs,
			TriggerExecutionContext dummyExecutionContext) {
		for(TriggerConfig config : configs) {
			if (config.getCondition() == null || config.getCondition().satisfiedBy(dummyExecutionContext))
				return config;
		}
		return null;
	}

	private Promise<BaseResponse, TriggerExecutionException> resolveDefault(DoneCallback<BaseResponse> doneCallback) {
		if (doneCallback != null)
			doneCallback.onDone(null);
		return new SimpleDonePromise<BaseResponse, TriggerExecutionException>(null);
	}
	
	private TriggerExecutionContext buildExecutionContext(String name, BaseRequest request, TriggerConfig config,
														  DoneCallback<BaseResponse> doneCallback, 
														  FailCallback<TriggerExecutionException> failCallback) {
		TriggerExecutionContextBuilder builder = 
				applicationContext.getInstance(TriggerExecutionContextBuilderFactory.class).create();
		
		builder.setManager(this).setConfig(config).setRequest(request)
			   .setApplicationContext(applicationContext)
			   .setDoneCallback(doneCallback)
			   .setFailCallback(failCallback)
			   .setEventName(name);
		
		return builder.build();
	}
	
	@Override
	public TriggerRegistration registerTrigger(String name) {
		return registerTrigger(name, new TriggerConfig());
	}

	@Override
	public TriggerRegistration registerTrigger(String name, TriggerConfig triggerConfig) {
		if (!triggerConfigs.containsKey(name))
			triggerConfigs.put(name, new ArrayList<>());
		triggerConfigs.get(name).add(triggerConfig);
		return triggerConfig;
	}
	
	@Override
	public TriggerRegistration registerPeriodicEvent(PeriodicTaskMessage msg) {
		return registerPeriodicEvent(msg, new TriggerConfig());
	}

	@Override
	public TriggerRegistration registerPeriodicEvent(PeriodicTaskMessage msg, TriggerConfig triggerConfig) {
		String name = UUID.randomUUID().toString();
		TriggerRegistration config = registerTrigger(name, triggerConfig);
		ScheduledFuture<?> future = scheduledExecutors.scheduleAtFixedRate(() -> {
			fire(name, msg.getRequest());
		}, msg.getDelay(), msg.getPeriod(), TimeUnit.MILLISECONDS);
		scheduledFutures.add(future);
		return config;
	}

	@Override
	public TriggerHandlingStrategy getHandlingStrategy() {
		return handlingStrategy;
	}
	
	@Override
	public void setHandlingStrategy(TriggerHandlingStrategy handlingStategy) {
		this.handlingStrategy = handlingStategy;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void shutdown() {
		for(ScheduledFuture<?> future : scheduledFutures) {
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
