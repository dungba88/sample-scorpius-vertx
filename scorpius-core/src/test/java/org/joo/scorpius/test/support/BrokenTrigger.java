package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.impl.AbstractTrigger;

public class BrokenTrigger extends AbstractTrigger<SampleRequest, BaseResponse> {

	@Override
	public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
		executionContext.verifyTraceId();
		SampleRequest theRequest = (SampleRequest) executionContext.getRequest();
		String name = theRequest.getName() + executionContext.getTraceId() + executionContext.fetchRawTraceId();
		throw new UnsupportedOperationException("broken " + name);
	}
}
