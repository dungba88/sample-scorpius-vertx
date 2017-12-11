package org.joo.scorpius.trigger.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerRegistration;
import org.joo.scorpius.trigger.TriggerRepository;

public class DefaultTriggerRepository implements TriggerRepository {

	private Map<String, List<TriggerConfig>> preStartTriggerConfigs;

	private Map<String, TriggerConfig[]> postStartTriggerConfigs;

	private AtomicBoolean started = new AtomicBoolean(false);

	public DefaultTriggerRepository() {
		this.preStartTriggerConfigs = new ConcurrentHashMap<>();
	}

	@Override
	public TriggerRegistration registerTrigger(String name, TriggerConfig triggerConfig) {
		if (started.get())
			throw new IllegalStateException("Repository is already started");

		if (!preStartTriggerConfigs.containsKey(name))
			preStartTriggerConfigs.put(name, new ArrayList<>());
		preStartTriggerConfigs.get(name).add(triggerConfig);
		return triggerConfig;
	}

	@Override
	public TriggerConfig[] getTriggerConfigs(String name) {
		return postStartTriggerConfigs.get(name);
	}

	@Override
	public void start() {
		if (!started.compareAndSet(false, true))
			throw new IllegalStateException("Repository is already started");

		postStartTriggerConfigs = new HashMap<>();
		for (String key : preStartTriggerConfigs.keySet()) {
			postStartTriggerConfigs.put(key, preStartTriggerConfigs.get(key).toArray(new TriggerConfig[0]));
		}
	}

	@Override
	public void shutdown() {

	}
}
