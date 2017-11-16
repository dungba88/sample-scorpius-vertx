package org.joo.scorpius.trigger.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.MalformedRequestException;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.deferred.DoneCallback;
import org.joo.scorpius.support.deferred.FailCallback;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.deferred.SimpleDonePromise;
import org.joo.scorpius.support.deferred.SimpleFailurePromise;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.TriggerRegistration;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultTriggerManager extends AbstractTriggerEventDispatcher implements TriggerManager {
	
	private Map<String, TriggerConfig> triggerConfigs;
	
	private ApplicationContext applicationContext;
	
	private TriggerHandlingStrategy handlingStrategy;
	
	public DefaultTriggerManager(ApplicationContext applicationContext) {
		this.triggerConfigs = new HashMap<>();
		this.applicationContext = applicationContext;
		this.handlingStrategy = new DefaultHandlingStrategy();
	}
	
	@Override
	public BaseRequest decodeRequestForEvent(String name, String data) throws MalformedRequestException {
		if (name == null)
			throw new MalformedRequestException("Event name is null");
		
		if (!triggerConfigs.containsKey(name))
			return null;

		TriggerConfig config = triggerConfigs.get(name);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (BaseRequest) mapper.readValue(data, config.getRequestClass());
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
		if (!triggerConfigs.containsKey(name)) {
			return resolveDefault();
		}
		
		if (!data.verifyTraceId()) {
			TriggerExecutionException ex = new TriggerExecutionException("TraceId has not been attached");
			if (failCallback != null)
				failCallback.onFail(ex);
			return new SimpleFailurePromise<BaseResponse, TriggerExecutionException>(ex);
		}
		
		TriggerExecutionContext executionContext = buildExecutionContext(name, data, doneCallback, failCallback);
		handlingStrategy.handle(executionContext);
		return executionContext.promise();
	}
	
	private Promise<BaseResponse, TriggerExecutionException> resolveDefault() {
		return new SimpleDonePromise<BaseResponse, TriggerExecutionException>(null);
	}

	private TriggerExecutionContext buildExecutionContext(String name, BaseRequest request, 
														  DoneCallback<BaseResponse> doneCallback, 
														  FailCallback<TriggerExecutionException> failCallback) {
		TriggerConfig config = triggerConfigs.get(name);
		
		TriggerExecutionContextBuilder builder = applicationContext.getExecutionContextBuilderFactory().create();
		
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
		if (triggerConfigs.containsKey(name))
			throw new IllegalArgumentException("Event " + name + " is already registered");
		triggerConfigs.put(name, triggerConfig);
		return triggerConfig;
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
}
