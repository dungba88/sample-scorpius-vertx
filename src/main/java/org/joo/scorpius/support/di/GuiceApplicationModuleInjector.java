package org.joo.scorpius.support.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceApplicationModuleInjector implements ApplicationModuleInjector {

	private Injector injector;
	
	public GuiceApplicationModuleInjector(Module... modules) {
		this.injector = Guice.createInjector(modules);
	}
	
	@Override
	public <T> T getInstance(Class<T> clazz) {
		return injector.getInstance(clazz);
	}
	
	public Injector getInternalInjector() {
		return injector;
	}

	@Override
	public <T> void override(Class<T> clazz, T instance) {
		throw new UnsupportedOperationException();
	}
}
