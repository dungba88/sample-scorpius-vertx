package org.joo.scorpius.support.builders;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.Deferred;
import org.joo.scorpius.support.deferred.DoneCallback;
import org.joo.scorpius.support.deferred.FailCallback;
import org.joo.scorpius.support.deferred.SimpleDeferredObject;
import org.joo.scorpius.trigger.DefaultTriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerManager;

public class TriggerExecutionContextBuilder implements Builder<TriggerExecutionContext> {
	
	private TriggerConfig config;
	
	private BaseRequest request;

	private ApplicationContext applicationContext;
	
	private FailCallback<TriggerExecutionException> failCallback;

	private DoneCallback<BaseResponse> doneCallback;
	
	private TriggerManager manager;
	
	@Override
	public TriggerExecutionContext build() {
		Deferred<BaseResponse, TriggerExecutionException> deferred = null;
		if (doneCallback != null || failCallback != null) {
			deferred = new SimpleDeferredObject<BaseResponse, TriggerExecutionException>(doneCallback, failCallback);
		} else {
			deferred = applicationContext.getDeferredFactory().create();
		}
		String id = applicationContext.getIdGenerator().create();
		return new DefaultTriggerExecutionContext(manager, config, request, applicationContext, deferred, id);
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

	public TriggerExecutionContextBuilder setDoneCallback(DoneCallback<BaseResponse> doneCallback) {
		this.doneCallback = doneCallback;
		return this;
	}

	public TriggerExecutionContextBuilder setFailCallback(FailCallback<TriggerExecutionException> failCallback) {
		this.failCallback = failCallback;
		return this;
	}

	public TriggerManager getManager() {
		return manager;
	}

	public TriggerExecutionContextBuilder setManager(TriggerManager manager) {
		this.manager = manager;
		return this;
	}
}
