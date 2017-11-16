package org.joo.scorpius.support.builders;

import java.util.Optional;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.builders.id.VoidIdGenerator;
import org.joo.scorpius.support.deferred.AsyncDeferredObject;
import org.joo.scorpius.support.deferred.Deferred;

public class ApplicationContextBuilder implements Builder<ApplicationContext> {
	
	private Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory;
	
	private Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory;
	
	private Factory<Optional<String>> idGenerator;
	
	public ApplicationContextBuilder() {
		deferredFactory = () -> new AsyncDeferredObject<>();
		executionContextBuilderFactory = () -> new TriggerExecutionContextBuilder();
		idGenerator = new VoidIdGenerator();
	}
	
	@Override
	public ApplicationContext build() {
		return new ApplicationContext(deferredFactory, executionContextBuilderFactory, getIdGenerator());
	}

	public Factory<Deferred<BaseResponse, TriggerExecutionException>> getDeferredFactory() {
		return deferredFactory;
	}

	public void setDeferredFactory(Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory) {
		this.deferredFactory = deferredFactory;
	}

	public Factory<TriggerExecutionContextBuilder> getExecutionContextBuilderFactory() {
		return executionContextBuilderFactory;
	}

	public void setExecutionContextBuilderFactory(Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory) {
		this.executionContextBuilderFactory = executionContextBuilderFactory;
	}

	public Factory<Optional<String>> getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(Factory<Optional<String>> idGenerator) {
		this.idGenerator = idGenerator;
	}
}
