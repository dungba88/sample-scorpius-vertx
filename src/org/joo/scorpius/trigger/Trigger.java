package org.joo.scorpius.trigger;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;

public interface Trigger<T extends BaseRequest, H extends BaseResponse> {

	public H run(BaseRequest request, ApplicationContext appContext);
}
