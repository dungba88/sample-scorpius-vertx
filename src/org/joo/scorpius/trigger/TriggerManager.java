package org.joo.scorpius.trigger;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

public interface TriggerManager {

	public BaseRequest decodeRequestForEvent(String name, String data);

	public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data);
	
	public void registerTrigger(String name, TriggerConfig triggerConfig);

	public TriggerHandlingStrategy getHandlingStrategy();

	public void setHandlingStrategy(TriggerHandlingStrategy handlingStategy);
}
