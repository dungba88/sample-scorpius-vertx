package org.joo.scorpius.test.vertx.sample;

import org.joo.scorpius.test.vertx.VertxApplicationContext;
import org.joo.scorpius.test.vertx.support.BaseRequest;
import org.joo.scorpius.test.vertx.support.BaseResponse;
import org.joo.scorpius.test.vertx.trigger.BaseTrigger;

public class SampleTrigger extends BaseTrigger<SampleRequest, BaseResponse> {

	@Override
	public BaseResponse run(BaseRequest request, VertxApplicationContext appContext) {
		SampleRequest theRequest = (SampleRequest) request;
		SampleResponse response = new SampleResponse();
		response.setName("Hi " + theRequest.getName());
		return response;
	}
}
