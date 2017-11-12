package org.joo.scorpius;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.builders.Factory;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.deferred.Deferred;

public class ApplicationContext {

	private Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory;
	
	private Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory;

	public ApplicationContext(Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory,
			Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory) {
		this.deferredFactory = deferredFactory;
		this.executionContextBuilderFactory = executionContextBuilderFactory;
	}

	public Factory<Deferred<BaseResponse, TriggerExecutionException>> getDeferredFactory() {
		return deferredFactory;
	}

	public Factory<TriggerExecutionContextBuilder> getExecutionContextBuilderFactory() {
		return executionContextBuilderFactory;
	}
}
