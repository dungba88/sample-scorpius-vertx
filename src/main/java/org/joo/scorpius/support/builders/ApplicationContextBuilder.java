package org.joo.scorpius.support.builders;

import org.joo.promise4j.impl.CompletableDeferredObject;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.builders.contracts.DeferredFactory;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.support.builders.contracts.TriggerExecutionContextBuilderFactory;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.builders.id.VoidIdGenerator;
import org.joo.scorpius.support.di.ApplicationModuleInjector;
import org.joo.scorpius.support.di.SimpleApplicationModuleInjector;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;

public class ApplicationContextBuilder implements Builder<ApplicationContext> {

    private ApplicationModuleInjector injector;

    public ApplicationContextBuilder() {
        injector = new SimpleApplicationModuleInjector(map -> {
            map.put(IdGenerator.class, new VoidIdGenerator());
            map.put(DeferredFactory.class, (DeferredFactory) (() -> new CompletableDeferredObject<>()));
            map.put(TriggerExecutionContextBuilderFactory.class,
                    (TriggerExecutionContextBuilderFactory) (() -> new TriggerExecutionContextBuilder()));
            map.put(TriggerHandlingStrategyFactory.class,
                    (TriggerHandlingStrategyFactory) (() -> new DefaultHandlingStrategy()));
        });
    }

    @Override
    public ApplicationContext build() {
        return new ApplicationContext(injector);
    }

    public ApplicationModuleInjector getInjector() {
        return injector;
    }

    public ApplicationContextBuilder setInjector(ApplicationModuleInjector injector) {
        this.injector = injector;
        return this;
    }
}
