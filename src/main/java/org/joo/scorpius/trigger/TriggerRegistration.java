package org.joo.scorpius.trigger;

import java.util.function.Supplier;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;

public interface TriggerRegistration {

    public TriggerRegistration withCondition(String condition);

    public TriggerRegistration withCondition(TriggerCondition condition);

    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(Trigger<T, H> action);

    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(
            Supplier<Trigger<T, H>> supplier);

    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(Class<Trigger<T, H>> clazz)
            throws InstantiationException, IllegalAccessException;
}
