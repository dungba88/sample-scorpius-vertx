package org.joo.scorpius.trigger;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.exception.TriggerExecutionException;

public interface Trigger<T extends BaseRequest, H extends BaseResponse> {
	
	public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException;

	default public String getName() {
		return getClass().getName();
	}
}
