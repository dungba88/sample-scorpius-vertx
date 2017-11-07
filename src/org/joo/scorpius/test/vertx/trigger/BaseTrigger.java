package org.joo.scorpius.test.vertx.trigger;

import org.joo.scorpius.test.vertx.support.BaseRequest;
import org.joo.scorpius.test.vertx.support.BaseResponse;

public abstract class BaseTrigger<T extends BaseRequest, H extends BaseResponse> implements Trigger<T, H> {

}
