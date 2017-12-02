package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.impl.AbstractTrigger;

import lombok.Getter;

public class RetryTrigger extends AbstractTrigger<SampleRequest, BaseResponse> {
    
    private @Getter int retries = 0;

	@Override
	public void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
	    retries++;
	    executionContext.fail(new TriggerExecutionException("for retry"));
	    System.out.println("retrying");
	}
}
