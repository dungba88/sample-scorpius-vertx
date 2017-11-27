package org.joo.scorpius;

import org.joo.scorpius.support.builders.contracts.DeferredFactory;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.support.di.ApplicationModuleInjector;

import lombok.Getter;

public class ApplicationContext implements ApplicationModuleInjector {

    private final @Getter ApplicationModuleInjector injector;
    
    private @Getter IdGenerator idGenerator;
    
    private @Getter DeferredFactory deferredFactory;

    public ApplicationContext(final ApplicationModuleInjector injector) {
        this.injector = injector;
        this.refreshCachedProperties();
    }

    @Override
    public <T> T getInstance(final Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public <T> void override(final Class<T> clazz, final T instance) {
        injector.override(clazz, instance);
        refreshCachedProperties();
    }

    private void refreshCachedProperties() {
        this.idGenerator = injector.getInstance(IdGenerator.class);
        this.deferredFactory = injector.getInstance(DeferredFactory.class);
    }
}
