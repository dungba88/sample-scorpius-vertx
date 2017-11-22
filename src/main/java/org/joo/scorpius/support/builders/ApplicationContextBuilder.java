package org.joo.scorpius.support.builders;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.di.ApplicationModuleInjector;
import org.joo.scorpius.support.di.DefaultGuiceInjectionModule;
import org.joo.scorpius.support.di.GuiceApplicationModuleInjector;

public class ApplicationContextBuilder implements Builder<ApplicationContext> {
	
	private ApplicationModuleInjector injector;
	
	public ApplicationContextBuilder() {
		injector = new GuiceApplicationModuleInjector(new DefaultGuiceInjectionModule());
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
