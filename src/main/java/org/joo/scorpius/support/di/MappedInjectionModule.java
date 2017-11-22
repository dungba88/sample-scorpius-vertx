package org.joo.scorpius.support.di;

import java.util.Map;

public interface MappedInjectionModule {

	public void configure(Map<Class<?>, Object> instancesMap);
}
