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

public class DefaultTriggerManager implements TriggerManager {
	
	private Map<String, TriggerConfig> triggerConfigs;
	
	private ApplicationContext applicationContext;
	
	private TriggerHandlingStrategy handlingStrategy;
	
	public DefaultTriggerManager(ApplicationContext applicationContext) {
		this.triggerConfigs = new HashMap<>();
		this.applicationContext = applicationContext;
		this.handlingStrategy = new DefaultHandlingStrategy();
	}
	
	@Override
	public BaseRequest decodeRequestForEvent(String name, String data) {
		TriggerConfig config = triggerConfigs.get(name);
		ObjectMapper mapper = new ObjectMapper();
		try {
			return (BaseRequest) mapper.readValue(data, config.getRequestClass());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data) {
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

	private TriggerExecutionContext buildExecutionContext(String name, BaseRequest request) {
		TriggerConfig config = triggerConfigs.get(name);
		
		TriggerExecutionContextBuilder builder = applicationContext.getExecutionContextBuilderFactory().create();
		
		builder.setConfig(config).setRequest(request)
			   .setApplicationContext(applicationContext);
		
		return builder.build();
	}

	@Override
	public void registerTrigger(String name, TriggerConfig triggerConfig) {
		if (triggerConfigs.containsKey(name))
			throw new IllegalArgumentException("Event " + name + " is already registered");
		triggerConfigs.put(name, triggerConfig);
	}

	@Override
	public TriggerHandlingStrategy getHandlingStrategy() {
		return handlingStrategy;
	}

	@Override
	public void setHandlingStrategy(TriggerHandlingStrategy handlingStategy) {
		this.handlingStrategy = handlingStategy;
	}
}
