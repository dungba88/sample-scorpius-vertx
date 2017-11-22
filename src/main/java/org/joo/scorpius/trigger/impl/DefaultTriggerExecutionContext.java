package org.joo.scorpius.trigger.impl;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.deferred.Deferred;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.ExecutionContextExceptionMessage;
import org.joo.scorpius.support.message.ExecutionContextFinishMessage;
import org.joo.scorpius.support.message.ExecutionContextStartMessage;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerEvent;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerExecutionStatus;
import org.joo.scorpius.trigger.TriggerManager;

public class DefaultTriggerExecutionContext implements TriggerExecutionContext {
	
	private final static Logger logger = LogManager.getLogger(DefaultTriggerExecutionContext.class);
	
	private String id;
	
	private String eventName;
	
	private TriggerConfig config;
	
	private BaseRequest request;
	
	private ApplicationContext applicationContext;
	
	private TriggerExecutionStatus status;
	
	private Deferred<BaseResponse, TriggerExecutionException> deferred;
	
	private TriggerManager manager;
	
	public DefaultTriggerExecutionContext(TriggerManager manager, TriggerConfig config, BaseRequest request, 
										  ApplicationContext applicationContext,
										  Deferred<BaseResponse, TriggerExecutionException> deferred,
										  String id, String eventName) {
		this.id = id;
		this.eventName = eventName;
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

		if (manager.isEventEnabled(TriggerEvent.START))
			manager.notifyEvent(TriggerEvent.START, new ExecutionContextStartMessage(id, eventName, request));
		
		try {
			config.getTrigger().execute(this);
		} catch (TriggerExecutionException e) {
			fail(e);
		} catch(Throwable e) {
			fail(new TriggerExecutionException(e));
		}
	}
	
	public void finish(BaseResponse response) {
		if (status == TriggerExecutionStatus.FINISHED)
			throw new IllegalStateException("Trigger is already finished");
		
		deferred.resolve(response);
		
		status = TriggerExecutionStatus.FINISHED;

		if (manager.isEventEnabled(TriggerEvent.FINISH))
			manager.notifyEvent(TriggerEvent.FINISH, new ExecutionContextFinishMessage(id, eventName, request, response));
	}
	
	public void fail(TriggerExecutionException ex) {
		logException(ex);
		
		if (status == TriggerExecutionStatus.FINISHED) return;

		deferred.reject(ex);
		
		status = TriggerExecutionStatus.FINISHED;
	}
	
	private void logException(TriggerExecutionException ex) {
		if (logger.isErrorEnabled()) {
			logger.error("Exception occured while executing trigger with event name {}", eventName, ex);
		}
		if (manager.isEventEnabled(TriggerEvent.EXCEPTION))
			manager.notifyEvent(TriggerEvent.EXCEPTION, new ExecutionContextExceptionMessage(id, eventName, request, ex));
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
	public void attachTraceId(Optional<String> traceId) {
		request.attachTraceId(traceId);
	}

	@Override
	public boolean verifyTraceId() {
		return request.verifyTraceId();
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public String getTraceId() {
		return request.getTraceId();
	}

	@Override
	public Optional<String> fetchRawTraceId() {
		return request.fetchRawTraceId();
	}
}
