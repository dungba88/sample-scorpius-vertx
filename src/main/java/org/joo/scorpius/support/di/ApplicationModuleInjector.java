package org.joo.scorpius.support.di;

public interface ApplicationModuleInjector {

	public <T> T getInstance(Class<T> clazz);
	
	public <T> void override(Class<T> clazz, T instance);
}
