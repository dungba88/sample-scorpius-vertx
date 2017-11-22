package org.joo.scorpius.support.di;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceApplicationModuleInjector implements ApplicationModuleInjector {

	private Injector injector;
	
	private ThreadLocal<Map<Class<?>, Object>> instancesMap;
	
	private boolean cache;
	
	public GuiceApplicationModuleInjector(Module... modules) {
		this(true, modules);
	}
	
	public GuiceApplicationModuleInjector(boolean cache, Module... modules) {
		this.cache = cache;
		this.injector = Guice.createInjector(modules);
		this.instancesMap = new ThreadLocal<Map<Class<?>, Object>>() {
			@Override public Map<Class<?>, Object> initialValue() {
	            return new HashMap<>();
	        }
		};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getInstance(Class<T> clazz) {
		if (!cache)
			return injector.getInstance(clazz);

		Map<Class<?>, Object> map = instancesMap.get();
		T instance = (T) map.get(clazz);
		if (instance != null) return instance;
		
		instance = injector.getInstance(clazz);
		map.put(clazz, instance);
		return instance;
	}
	
	public Injector getInternalInjector() {
		return injector;
	}

	@Override
	public <T> void override(Class<T> clazz, T instance) {
		throw new UnsupportedOperationException();
	}
}
