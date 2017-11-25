package org.joo.scorpius;

import org.joo.scorpius.support.di.ApplicationModuleInjector;

import lombok.Getter;

public class ApplicationContext implements ApplicationModuleInjector {

    private final @Getter ApplicationModuleInjector injector;

    public ApplicationContext(final ApplicationModuleInjector injector) {
        this.injector = injector;
    }

    @Override
    public <T> T getInstance(final Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public <T> void override(final Class<T> clazz, final T instance) {
        injector.override(clazz, instance);
    }
}
