package org.joo.scorpius.trigger;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;

public abstract class AbstractTrigger<T extends BaseRequest, H extends BaseResponse> implements Trigger<T, H> {

}
