package org.joo.scorpius;

import java.util.Optional;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.builders.Factory;
import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.deferred.Deferred;

public class ApplicationContext {

	private Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory;
	
	private Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory;

	private Factory<Optional<String>> idGenerator;
	
	public ApplicationContext(Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory,
			Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory,
			Factory<Optional<String>> idGenerator) {
		this.deferredFactory = deferredFactory;
		this.executionContextBuilderFactory = executionContextBuilderFactory;
		this.idGenerator = idGenerator;
	}

	public Factory<Deferred<BaseResponse, TriggerExecutionException>> getDeferredFactory() {
		return deferredFactory;
	}

	public Factory<TriggerExecutionContextBuilder> getExecutionContextBuilderFactory() {
		return executionContextBuilderFactory;
	}

	public Factory<Optional<String>> getIdGenerator() {
		return idGenerator;
	}
}
