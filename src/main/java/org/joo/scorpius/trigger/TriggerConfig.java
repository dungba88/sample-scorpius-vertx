package org.joo.scorpius.trigger;

import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.trigger.impl.SqlTriggerCondition;

public class TriggerConfig implements TriggerRegistration {

    private Trigger<? extends BaseRequest, ? extends BaseResponse> trigger;

    private Class<?> requestClass;

    private TriggerCondition condition;

    public TriggerConfig() {

    }

    public TriggerConfig(Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
        this.withAction(trigger);
    }

    private Class<?> getRequestClassFor(Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
        return ((Class<?>) ((ParameterizedType) trigger.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @Override
    public TriggerRegistration withCondition(String condition) {
        this.condition = new SqlTriggerCondition(condition);
        return this;
    }

    @Override
    public TriggerRegistration withCondition(TriggerCondition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(Trigger<T, H> trigger) {
        this.trigger = trigger;
        this.requestClass = getRequestClassFor(trigger);
        return this;
    }

    @Override
    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(
            Supplier<Trigger<T, H>> supplier) {
        return withAction(supplier.get());
    }

    @Override
    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(Class<Trigger<T, H>> clazz)
            throws InstantiationException, IllegalAccessException {
        return withAction(clazz.newInstance());
    }

    public TriggerCondition getCondition() {
        return condition;
    }

    public Trigger<? extends BaseRequest, ? extends BaseResponse> getTrigger() {
        return trigger;
    }

    public Class<?> getRequestClass() {
        return requestClass;
    }
}
