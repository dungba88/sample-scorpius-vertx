package org.joo.scorpius.test.vertx.trigger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.joo.scorpius.test.vertx.VertxApplicationContext;
import org.joo.scorpius.test.vertx.support.BaseRequest;
import org.joo.scorpius.test.vertx.support.BaseResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TriggerManager {
	
	private Map<String, TriggerConfig> triggerConfigs;
	
	private VertxApplicationContext applicationContext;
	
	public TriggerManager(VertxApplicationContext applicationContext) {
		this.triggerConfigs = new HashMap<>();
		this.applicationContext = applicationContext;
	}

	public void fire(String name, String data, Consumer<BaseResponse> responseConsumer) {
		if (!triggerConfigs.containsKey(name)) {
			responseConsumer.accept(null);
			return;
		}
		TriggerConfig config = triggerConfigs.get(name);
		BaseRequest request = decodeBaseRequest(data, config.getRequestClass());
		CompletableFuture.supplyAsync(() -> config.getTrigger().run(request, applicationContext))
						.thenAccept(responseConsumer);
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
}
