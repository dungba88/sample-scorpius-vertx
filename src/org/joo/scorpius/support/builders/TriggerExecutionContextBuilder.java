package org.joo.scorpius.support.builders;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.trigger.DefaultTriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class TriggerExecutionContextBuilder implements Builder<TriggerExecutionContext> {
	
	private TriggerConfig config;
	
	private BaseRequest request;

	private ApplicationContext applicationContext;

	@Override
	public TriggerExecutionContext build() {
		return new DefaultTriggerExecutionContext(config, request, applicationContext);
	}

	public BaseRequest getRequest() {
		return request;
	}

	public TriggerExecutionContextBuilder setRequest(BaseRequest request) {
		this.request = request;
		return this;
	}

	public TriggerConfig getConfig() {
		return config;
	}

	public TriggerExecutionContextBuilder setConfig(TriggerConfig config) {
		this.config = config;
		return this;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public TriggerExecutionContextBuilder setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		return this;
	}
}
