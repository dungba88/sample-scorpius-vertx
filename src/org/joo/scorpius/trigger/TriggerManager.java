package org.joo.scorpius.trigger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.deferred.SimpleDonePromise;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TriggerManager {
	
	private Map<String, TriggerConfig> triggerConfigs;
	
	private ApplicationContext applicationContext;
	
	private TriggerHandlingStrategy handlingStategy;
	
	public TriggerManager(ApplicationContext applicationContext) {
		this.triggerConfigs = new HashMap<>();
		this.applicationContext = applicationContext;
		this.handlingStategy = new DefaultHandlingStrategy();
	}
	
	public Promise<BaseResponse, TriggerExecutionException> fire(String name, String data) {
		return fire(name, data, handlingStategy);
	}

	public Promise<BaseResponse, TriggerExecutionException> fire(String name, String data, TriggerHandlingStrategy handlingStrategy) {
		if (!triggerConfigs.containsKey(name)) {
			return resolveDefault();
		}
		
		TriggerExecutionContext executionContext = buildExecutionContext(name, data);
		handlingStrategy.handle(executionContext);
		return executionContext.promise();
	}
	
	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data) {
		return fire(name, data, handlingStategy);
	}
	
	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data, TriggerHandlingStrategy handlingStrategy) {
		if (!triggerConfigs.containsKey(name)) {
			return resolveDefault();
		}
		
		TriggerExecutionContext executionContext = buildExecutionContext(name, data);
		handlingStrategy.handle(executionContext);
		return executionContext.promise();
	}
	
	private Promise<BaseResponse, TriggerExecutionException> resolveDefault() {
		return new SimpleDonePromise<BaseResponse, TriggerExecutionException>(null);
	}

	private TriggerExecutionContext buildExecutionContext(String name, String data) {
		TriggerConfig config = triggerConfigs.get(name);
		BaseRequest request = decodeBaseRequest(data, config.getRequestClass());
		return buildExecutionContext(name, request);
	}

	private BaseRequest decodeBaseRequest(String data, Class<?> requestClass) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (BaseRequest) mapper.readValue(data, requestClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private TriggerExecutionContext buildExecutionContext(String name, BaseRequest request) {
		TriggerConfig config = triggerConfigs.get(name);
		
		TriggerExecutionContextBuilder builder = applicationContext.getExecutionContextBuilderFactory().create();
		
		builder.setConfig(config).setRequest(request)
			   .setApplicationContext(applicationContext);
		
		return builder.build();
	}

	public void registerTrigger(String name, TriggerConfig triggerConfig) {
		if (triggerConfigs.containsKey(name))
			throw new IllegalArgumentException("Event " + name + " is already registered");
		triggerConfigs.put(name, triggerConfig);
	}

	public TriggerHandlingStrategy getHandlingStategy() {
		return handlingStategy;
	}

	public void setHandlingStategy(TriggerHandlingStrategy handlingStategy) {
		this.handlingStategy = handlingStategy;
	}
}
