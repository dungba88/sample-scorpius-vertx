package org.joo.scorpius.support.di;

import com.google.inject.Module;

public interface ApplicationModuleInjector {

	public <T> T getInstance(Class<T> clazz);

	public ApplicationModuleInjector applyModules(Module...modules);
}
