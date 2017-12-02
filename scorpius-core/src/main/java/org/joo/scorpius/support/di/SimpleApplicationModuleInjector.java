package org.joo.scorpius.support.di;

import java.util.HashMap;
import java.util.Map;

public class SimpleApplicationModuleInjector implements ApplicationModuleInjector {

    private Map<Class<?>, Object> instancesMap;

    public SimpleApplicationModuleInjector() {
        this.instancesMap = new HashMap<>();
    }

    public SimpleApplicationModuleInjector(final Map<Class<?>, Object> instancesMap) {
        this.instancesMap = new HashMap<>(instancesMap);
    }

    public SimpleApplicationModuleInjector(final MappedInjectionModule... modules) {
        this.instancesMap = new HashMap<>();
        for (MappedInjectionModule module : modules) {
            module.configure(this.instancesMap);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(final Class<T> clazz) {
        return (T) instancesMap.get(clazz);
    }

    @Override
    public <T> void override(final Class<T> clazz, final T instance) {
        this.instancesMap.put(clazz, instance);
    }
}
