package org.joo.scorpius;

import org.joo.scorpius.support.builders.Factory;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.deferred.Deferred;
import org.joo.scorpius.support.deferred.SyncDeferredObject;

public class ApplicationContext {

	private Factory<Deferred> deferredFactory;
	
	private Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory;
	
	public ApplicationContext() {
		deferredFactory = () -> new SyncDeferredObject<>();
		executionContextBuilderFactory = () -> new TriggerExecutionContextBuilder();
	}

	public Factory<Deferred> getDeferredFactory() {
		return deferredFactory;
	}

	public void setDeferredFactory(Factory<Deferred> deferredFactory) {
		this.deferredFactory = deferredFactory;
	}

	public Factory<TriggerExecutionContextBuilder> getExecutionContextBuilderFactory() {
		return executionContextBuilderFactory;
	}

	public void setExecutionContextBuilderFactory(Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory) {
		this.executionContextBuilderFactory = executionContextBuilderFactory;
	}
}
