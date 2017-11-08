package org.joo.scorpius.trigger;

import org.jdeferred.Promise;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;

public interface TriggerExecutionContext {

	public void pending();
	
	public void execute();
	
	public void finish(BaseResponse response);
	
	public void reject(TriggerExecutionException ex);
	
	public Promise<BaseResponse, TriggerExecutionException, Object> promise();

	public TriggerConfig getConfig();

	public BaseRequest getRequest();

	public TriggerExecutionStatus getStatus();

	public ApplicationContext getApplicationContext();
}
