package org.joo.scorpius.trigger;

import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.trigger.impl.SqlTriggerCondition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TriggerConfig implements TriggerRegistration {

    private Trigger<? extends BaseRequest, ? extends BaseResponse> trigger;

    private Class<?> requestClass;

    private TriggerCondition condition;

    public TriggerConfig() {

    }

    public TriggerConfig(final Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
        this.withAction(trigger);
    }

    private Class<?> getRequestClassFor(final Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
        return ((Class<?>) ((ParameterizedType) trigger.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @Override
    public TriggerRegistration withCondition(final String condition) {
        this.condition = new SqlTriggerCondition(condition);
        return this;
    }

    @Override
    public TriggerRegistration withCondition(final TriggerCondition condition) {
        this.condition = condition;
        return this;
    }

    @Override
    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(final Trigger<T, H> trigger) {
        this.trigger = trigger;
        this.requestClass = getRequestClassFor(trigger);
        return this;
    }

    @Override
    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(
            final Supplier<Trigger<T, H>> supplier) {
        return withAction(supplier.get());
    }

    @Override
    public <T extends BaseRequest, H extends BaseResponse> TriggerRegistration withAction(
            final Class<? extends Trigger<T, H>> clazz) throws InstantiationException, IllegalAccessException {
        return withAction(clazz.newInstance());
    }
}
