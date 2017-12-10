package org.joo.scorpius.support.bootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joo.scorpius.Bootstrap;

import lombok.Getter;

public class CompositionBootstrap extends AbstractBootstrap {

	private @Getter List<Bootstrap> bootstraps = new CopyOnWriteArrayList<>();

	public CompositionBootstrap() {

	}

	public CompositionBootstrap(Bootstrap... bootstraps) {
		this.bootstraps.addAll(Arrays.asList(bootstraps));
	}

	@Override
	public void run() {
		configureBootstraps(bootstraps);
		for (Bootstrap bootstrap : bootstraps) {
			bootstrap.setApplicationContext(applicationContext);
			bootstrap.setTriggerManager(triggerManager);
			bootstrap.run();
		}
	}

	protected void configureBootstraps(List<Bootstrap> bootstraps) {
		// do nothing
	}

	@Override
	public void shutdown() {
		for (Bootstrap bootstrap : bootstraps) {
			bootstrap.shutdown();
		}
	}
}
