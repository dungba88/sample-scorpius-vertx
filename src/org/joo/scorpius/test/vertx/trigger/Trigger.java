package org.joo.scorpius.test.vertx.trigger;

import org.joo.scorpius.test.vertx.ApplicationContext;
import org.joo.scorpius.test.vertx.support.BaseRequest;
import org.joo.scorpius.test.vertx.support.BaseResponse;

public interface Trigger<T extends BaseRequest, H extends BaseResponse> {

	public H run(BaseRequest request, ApplicationContext appContext);
}
