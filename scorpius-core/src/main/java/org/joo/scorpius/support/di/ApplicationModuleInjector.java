package org.joo.scorpius.support.di;

public interface ApplicationModuleInjector {

    public <T> T getInstance(final Class<T> clazz);

    public <T> void override(final Class<T> clazz, final T instance);
}
