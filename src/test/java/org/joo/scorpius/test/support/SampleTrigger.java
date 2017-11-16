package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.impl.AbstractTrigger;

public class SampleTrigger extends AbstractTrigger<SampleRequest, BaseResponse> {

	@Override
	public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
		SampleRequest theRequest = (SampleRequest) executionContext.getRequest();
		executionContext.finish(new SampleResponse("Hi " + theRequest.getName()));
	}
}
