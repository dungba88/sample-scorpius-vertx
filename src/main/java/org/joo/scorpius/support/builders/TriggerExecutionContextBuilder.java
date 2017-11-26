package org.joo.scorpius.support.builders;

import java.util.Optional;

import org.joo.promise4j.Deferred;
import org.joo.promise4j.DoneCallback;
import org.joo.promise4j.FailCallback;
import org.joo.promise4j.impl.SimpleDeferredObject;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.builders.contracts.DeferredFactory;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerExecutionContext;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.impl.DefaultTriggerExecutionContext;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TriggerExecutionContextBuilder implements Builder<TriggerExecutionContext> {

    private TriggerConfig config;

    private BaseRequest request;

    private ApplicationContext applicationContext;

    private FailCallback<TriggerExecutionException> failCallback;

    private DoneCallback<BaseResponse> doneCallback;

    private TriggerManager manager;

    private String eventName;

    @Override
    public TriggerExecutionContext build() {
        Deferred<BaseResponse, TriggerExecutionException> deferred = null;
        if (doneCallback != null || failCallback != null) {
            deferred = new SimpleDeferredObject<>(doneCallback, failCallback);
        } else {
            deferred = applicationContext.getInstance(DeferredFactory.class).create();
        }
        Optional<String> id = applicationContext.getInstance(IdGenerator.class).create();
        return new DefaultTriggerExecutionContext(manager, config, request, applicationContext, deferred,
                id.orElse(null), eventName);
    }
}
