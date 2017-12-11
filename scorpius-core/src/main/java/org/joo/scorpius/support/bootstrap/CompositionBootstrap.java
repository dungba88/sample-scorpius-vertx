package org.joo.scorpius.support.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.JoinedPromise;
import org.joo.promise4j.impl.JoinedResults;
import org.joo.scorpius.Bootstrap;

import lombok.Getter;

public class CompositionBootstrap extends AbstractBootstrap<JoinedResults<Object>> {

	private @Getter List<Bootstrap<?>> bootstraps = new CopyOnWriteArrayList<>();

	public CompositionBootstrap() {

	}

	public CompositionBootstrap(Bootstrap<?>... bootstraps) {
		this.bootstraps.addAll(Arrays.asList(bootstraps));
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Promise<JoinedResults<Object>, Throwable> run() {
		configureBootstraps(bootstraps);
		List<Promise<Object, Throwable>> promises = new ArrayList<>();
		for (Bootstrap bootstrap : bootstraps) {
			bootstrap.setApplicationContext(applicationContext);
			bootstrap.setTriggerManager(triggerManager);
			Promise promise = bootstrap.run();
			promises.add(promise);
		}
		return JoinedPromise.from(promises);
	}

	protected void configureBootstraps(List<Bootstrap<?>> bootstraps) {
		// do nothing
	}

	@Override
	public void shutdown() {
		for (Bootstrap<?> bootstrap : bootstraps) {
			bootstrap.shutdown();
		}
	}
}
