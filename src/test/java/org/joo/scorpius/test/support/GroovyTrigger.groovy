package org.joo.scorpius.test.support

import org.joo.scorpius.support.TriggerExecutionException
import org.joo.scorpius.trigger.AbstractTrigger
import org.joo.scorpius.trigger.TriggerExecutionContext

class GroovyTrigger extends AbstractTrigger<SampleRequest, SampleResponse> {

	void execute(TriggerExecutionContext executionContext) throws TriggerExecutionException {
		def request = (SampleRequest)executionContext.getRequest()
		executionContext.finish(new SampleResponse(request.getName()))
	}
}
