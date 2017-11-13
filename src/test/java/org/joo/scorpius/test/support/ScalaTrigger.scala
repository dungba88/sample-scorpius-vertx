package org.joo.scorpius.test.support

import org.joo.scorpius.trigger.AbstractTrigger
import org.joo.scorpius.trigger.TriggerExecutionContext

class ScalaTrigger extends AbstractTrigger[SampleRequest, SampleResponse] {
  
  def execute(executionContext: TriggerExecutionContext) {
    val request : SampleRequest = executionContext.getRequest().asInstanceOf[SampleRequest]
    executionContext.finish(new SampleResponse(request.getName()))
  }
}