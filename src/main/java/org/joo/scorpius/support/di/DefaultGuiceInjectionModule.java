package org.joo.scorpius.support.di;

import org.joo.scorpius.support.builders.TriggerExecutionContextBuilder;
import org.joo.scorpius.support.builders.contracts.DeferredFactory;
import org.joo.scorpius.support.builders.contracts.TriggerExecutionContextBuilderFactory;
import org.joo.scorpius.support.builders.contracts.TriggerHandlingStrategyFactory;
import org.joo.scorpius.support.builders.id.IdGenerator;
import org.joo.scorpius.support.builders.id.VoidIdGenerator;
import org.joo.scorpius.support.deferred.AsyncDeferredObject;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;

import com.google.inject.Binder;
import com.google.inject.Module;

public class DefaultGuiceInjectionModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(IdGenerator.class).to(VoidIdGenerator.class);
		binder.bind(DeferredFactory.class).toInstance(() -> new AsyncDeferredObject<>());
		binder.bind(TriggerExecutionContextBuilderFactory.class).toInstance(() -> new TriggerExecutionContextBuilder());
		binder.bind(TriggerHandlingStrategyFactory.class).toInstance(() -> new DefaultHandlingStrategy());
	}
}
