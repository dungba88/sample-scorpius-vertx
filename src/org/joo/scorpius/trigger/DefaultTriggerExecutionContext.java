package org.joo.scorpius.trigger;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.Deferred;
import org.joo.scorpius.support.deferred.Promise;

public class DefaultTriggerExecutionContext implements TriggerExecutionContext {
	
	private TriggerConfig config;
	
	private BaseRequest request;
	
	private ApplicationContext applicationContext;
	
	private TriggerExecutionStatus status;
	
	private Deferred<BaseResponse, TriggerExecutionException> deferred;
	
	public DefaultTriggerExecutionContext(TriggerConfig config, BaseRequest request, 
										  ApplicationContext applicationContext,
										  Deferred<BaseResponse, TriggerExecutionException> deferred) {
		this.config = config;
		this.request = request;
		this.applicationContext = applicationContext;
		this.status = TriggerExecutionStatus.CREATED;
		this.deferred = deferred;
	}
	
	public void pending() {
		status = TriggerExecutionStatus.PENDING;
	}
	
	public void execute() {
		if (status == TriggerExecutionStatus.EXECUTING || status == TriggerExecutionStatus.FINISHED) {
			throw new IllegalAccessError("Trigger is already running or finished");
		}
		if (config.getTrigger() == null)
			return;
		try {
			config.getTrigger().execute(this);
		} catch (TriggerExecutionException e) {
			fail(e);
		}
	}
	
	public void finish(BaseResponse response) {
		if (status == TriggerExecutionStatus.FINISHED)
			throw new IllegalAccessError("Trigger is already finished");
		deferred.resolve(response);
	}
	
	public void fail(TriggerExecutionException ex) {
		if (status == TriggerExecutionStatus.FINISHED)
			throw new IllegalAccessError("Trigger is already finished");
		deferred.reject(ex);
	}
	
	public Promise<BaseResponse, TriggerExecutionException> promise() {
		return deferred.promise();
	}

	public TriggerConfig getConfig() {
		return config;
	}

	public BaseRequest getRequest() {
		return request;
	}

	public TriggerExecutionStatus getStatus() {
		return status;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
