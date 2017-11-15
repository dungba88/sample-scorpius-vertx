package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.trigger.AbstractTrigger;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class NestedTrigger extends AbstractTrigger<NestedRequest, BaseResponse> {

	@Override
	public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
		final NestedRequest request = (NestedRequest) executionContext.getRequest();
		executionContext.getTriggerManager().fire(request.getName(), new SampleRequest("World"), 
				response -> executionContext.finish(response),
				ex -> executionContext.fail(ex));
	}
}
