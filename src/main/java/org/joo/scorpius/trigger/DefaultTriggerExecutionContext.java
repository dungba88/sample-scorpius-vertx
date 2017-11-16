package org.joo.scorpius.trigger;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.Deferred;
import org.joo.scorpius.support.deferred.Promise;

public class DefaultTriggerExecutionContext implements TriggerExecutionContext {
	
	private String id;
	
	private TriggerConfig config;
	
	private BaseRequest request;
	
	private ApplicationContext applicationContext;
	
	private TriggerExecutionStatus status;
	
	private Deferred<BaseResponse, TriggerExecutionException> deferred;
	
	private TriggerManager manager;
	
	public DefaultTriggerExecutionContext(TriggerManager manager, TriggerConfig config, BaseRequest request, 
										  ApplicationContext applicationContext,
										  Deferred<BaseResponse, TriggerExecutionException> deferred,
										  String id) {
		this.id = id;
		this.manager = manager;
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
			throw new IllegalStateException("Trigger is already running or finished");
		}
		if (config.getTrigger() == null)
			return;
		
		try {
			config.getTrigger().execute(this);
		} catch (TriggerExecutionException e) {
			fail(e);
		} catch(Throwable e) {
			fail(new TriggerExecutionException(e));
		} finally {
			//TODO: log
		}
	}
	
	public void finish(BaseResponse response) {
		if (status == TriggerExecutionStatus.FINISHED)
			throw new IllegalStateException("Trigger is already finished");
		deferred.resolve(response);
	}
	
	public void fail(TriggerExecutionException ex) {
		//TODO: log
		if (status == TriggerExecutionStatus.FINISHED) return;
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

	@Override
	public TriggerManager getTriggerManager() {
		return manager;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTraceId() {
		return request.getTraceId();
	}

	@Override
	public void attachTraceId(String traceId) {
		request.attachTraceId(traceId);
	}

	@Override
	public boolean verifyTraceId() {
		return request.verifyTraceId();
	}
}
