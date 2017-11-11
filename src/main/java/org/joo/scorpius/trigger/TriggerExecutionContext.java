package org.joo.scorpius.trigger;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.Promise;

public interface TriggerExecutionContext {

	public void pending();
	
	public void execute();
	
	public void finish(BaseResponse response);
	
	public void fail(TriggerExecutionException ex);
	
	public Promise<BaseResponse, TriggerExecutionException> promise();

	public TriggerConfig getConfig();

	public BaseRequest getRequest();

	public TriggerExecutionStatus getStatus();

	public ApplicationContext getApplicationContext();
}
