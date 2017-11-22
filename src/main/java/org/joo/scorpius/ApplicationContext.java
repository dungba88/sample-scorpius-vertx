package org.joo.scorpius;

import org.joo.scorpius.support.di.ApplicationModuleInjector;

import com.google.inject.Module;

public class ApplicationContext implements ApplicationModuleInjector {

	private final ApplicationModuleInjector injector;

	public ApplicationContext(ApplicationModuleInjector injector) {
		this.injector = injector;
	}
	
	public ApplicationModuleInjector getInjector() {
		return injector;
	}

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}

	@Override
	public ApplicationModuleInjector applyModules(Module... modules) {
		injector.applyModules(modules);
		return this;
	}
}
