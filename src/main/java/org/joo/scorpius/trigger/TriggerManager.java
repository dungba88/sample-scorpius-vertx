package org.joo.scorpius.trigger;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.MalformedRequestException;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.DoneCallback;
import org.joo.scorpius.support.deferred.FailCallback;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.message.PeriodicTaskMessage;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

public interface TriggerManager extends TriggerEventDispatcher {

	public BaseRequest decodeRequestForEvent(String name, String data) throws MalformedRequestException;

	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data);

	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data,
			DoneCallback<BaseResponse> doneCallback, FailCallback<TriggerExecutionException> failCallback);
	
	public TriggerRegistration registerPeriodicEvent(PeriodicTaskMessage msg);
	
	public TriggerRegistration registerPeriodicEvent(PeriodicTaskMessage msg, TriggerConfig triggerConfig);

	public TriggerRegistration registerTrigger(String name);

	public TriggerRegistration registerTrigger(String name, TriggerConfig triggerConfig);
	
	public ApplicationContext getApplicationContext();

	public TriggerHandlingStrategy getHandlingStrategy();

	public void setHandlingStrategy(TriggerHandlingStrategy handlingStategy);
	
	public void shutdown();
}
