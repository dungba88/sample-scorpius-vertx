package org.joo.scorpius.trigger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TriggerManager {
	
	private Map<String, TriggerConfig> triggerConfigs;
	
	private ApplicationContext applicationContext;
	
	private TriggerExecutionContextBuilder triggerExecutionContextBuilder;

	private TriggerHandlingStrategy handlingStategy;
	
	public TriggerManager(ApplicationContext applicationContext) {
		this.triggerConfigs = new HashMap<>();
		this.applicationContext = applicationContext;
		this.triggerExecutionContextBuilder = new TriggerExecutionContextBuilder();
		this.handlingStategy = new DefaultHandlingStrategy();
	}
	
	public Promise<BaseResponse, TriggerExecutionException, Object> fire(String name, String data) {
		return fire(name, data, handlingStategy);
	}

	public Promise<BaseResponse, TriggerExecutionException, Object> fire(String name, String data, TriggerHandlingStrategy handlingStrategy) {
		if (!triggerConfigs.containsKey(name)) {
			return resolveDefault();
		}
		
		TriggerExecutionContext executionContext = buildExecutionContext(name, data);
		handlingStrategy.handle(executionContext);
		return executionContext.promise();
	}
	
	public Promise<BaseResponse, TriggerExecutionException, Object> fire(String name, BaseRequest data) {
		return fire(name, data, handlingStategy);
	}
	
	public Promise<BaseResponse, TriggerExecutionException, Object> fire(String name, BaseRequest data, TriggerHandlingStrategy handlingStrategy) {
		if (!triggerConfigs.containsKey(name)) {
			return resolveDefault();
		}
		
		TriggerExecutionContext executionContext = buildExecutionContext(name, data);
		handlingStrategy.handle(executionContext);
		return executionContext.promise();
	}
	
	private Promise<BaseResponse, TriggerExecutionException, Object> resolveDefault() {
		Deferred<BaseResponse, TriggerExecutionException, Object> deferred = new DeferredObject<>();
		deferred.resolve(null);
		return deferred.promise();
	}

	private TriggerExecutionContext buildExecutionContext(String name, Object data) {
		TriggerConfig config = triggerConfigs.get(name);
		BaseRequest request = (data instanceof BaseRequest) ? (BaseRequest)data : decodeBaseRequest(data.toString(), config.getRequestClass());
		
		triggerExecutionContextBuilder.setConfig(config);
		triggerExecutionContextBuilder.setRequest(request);
		triggerExecutionContextBuilder.setApplicationContext(applicationContext);
		
		return triggerExecutionContextBuilder.build();
	}

	private BaseRequest decodeBaseRequest(String data, Class<?> requestClass) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (BaseRequest) mapper.readValue(data, requestClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
