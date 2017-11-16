package org.joo.scorpius.trigger.impl;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.trigger.Trigger;

public abstract class AbstractTrigger<T extends BaseRequest, H extends BaseResponse> implements Trigger<T, H> {

}
