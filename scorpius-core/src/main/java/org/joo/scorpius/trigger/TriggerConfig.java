package org.joo.scorpius.trigger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;
import org.joo.scorpius.trigger.impl.SqlTriggerCondition;

import lombok.Getter;
import net.jodah.failsafe.SyncFailsafe;

@Getter
public class TriggerConfig implements TriggerRegistration {

    private Trigger<? extends BaseRequest, ? extends BaseResponse> trigger;

    private Class<?> requestClass;

    private TriggerCondition condition;

    private SyncFailsafe<Object> failSafe;

    private TriggerHandlingStrategy strategy;

    public TriggerConfig() {

    }

    public TriggerConfig(final Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
        this.withAction(trigger);
    }

    private Class<?> getRequestClassFor(final Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
        Type superClassType = trigger.getClass().getGenericSuperclass();
        if (!(superClassType instanceof ParameterizedType))
            return BaseRequest.class;
        return ((Class<?>) ((ParameterizedType) superClassType).getActualTypeArguments()[0]);
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

    @Override
    public TriggerRegistration withFailSafe(final SyncFailsafe<Object> failSafe) {
        this.failSafe = failSafe;
        return this;
    }

    @Override
    public TriggerRegistration withHandlingStrategy(final TriggerHandlingStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
}
