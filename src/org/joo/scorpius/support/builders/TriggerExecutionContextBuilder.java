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

	public void setRequest(BaseRequest request) {
		this.request = request;
	}

	public TriggerConfig getConfig() {
		return config;
	}

	public void setConfig(TriggerConfig config) {
		this.config = config;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
