package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.impl.AbstractTrigger;

public class NestedTrigger extends AbstractTrigger<NestedRequest, BaseResponse> {

	@Override
	public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
		final NestedRequest request = (NestedRequest) executionContext.getRequest();
		final SampleRequest fireRequest = new SampleRequest("World");
		fireRequest.attachTraceId(request.getTraceId());
		executionContext.getTriggerManager().fire(request.getName(), fireRequest, 
				response -> executionContext.finish(response),
				ex -> executionContext.fail(ex));
	}
}
