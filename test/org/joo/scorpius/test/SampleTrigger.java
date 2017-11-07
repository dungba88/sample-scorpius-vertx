package org.joo.scorpius.test;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.trigger.AbstractTrigger;

public class SampleTrigger extends AbstractTrigger<SampleRequest, BaseResponse> {

	@Override
	public BaseResponse run(BaseRequest request, ApplicationContext appContext) {
		SampleRequest theRequest = (SampleRequest) request;
		SampleResponse response = new SampleResponse();
		response.setName("Hi " + theRequest.getName());
		return response;
	}
}
