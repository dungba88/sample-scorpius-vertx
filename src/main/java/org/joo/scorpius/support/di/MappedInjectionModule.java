package org.joo.scorpius.support.di;

import java.util.Map;

public interface MappedInjectionModule {

    public void configure(final Map<Class<?>, Object> instancesMap);
}
