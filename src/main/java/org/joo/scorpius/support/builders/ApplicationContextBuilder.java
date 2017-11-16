package org.joo.scorpius.support.builders;

import java.util.UUID;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;
import org.joo.scorpius.support.deferred.AsyncDeferredObject;
import org.joo.scorpius.support.deferred.Deferred;

public class ApplicationContextBuilder implements Builder<ApplicationContext> {
	
	private Factory<Deferred<BaseResponse, TriggerExecutionException>> deferredFactory;
	
	private Factory<TriggerExecutionContextBuilder> executionContextBuilderFactory;
	
	private Factory<String> idGenerator;
	
	public ApplicationContextBuilder() {
		deferredFactory = () -> new AsyncDeferredObject<>();
		executionContextBuilderFactory = () -> new TriggerExecutionContextBuilder();
		idGenerator = () -> UUID.randomUUID().toString();
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

	public Factory<String> getIdGenerator() {
		return idGenerator;
	}

	public void setIdGenerator(Factory<String> idGenerator) {
		this.idGenerator = idGenerator;
	}
}
