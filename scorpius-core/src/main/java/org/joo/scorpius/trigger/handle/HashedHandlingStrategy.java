package org.joo.scorpius.trigger.handle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.joo.scorpius.support.exception.NoMatchingRouteException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerExecutionContext;

import lombok.NonNull;

public class HashedHandlingStrategy<T> extends AbstractTriggerHandlingStrategy {

    private Function<TriggerExecutionContext, T> hashFunction;

    private Map<Object, TriggerHandlingStrategy> strategies;

    public HashedHandlingStrategy(final @NonNull Function<TriggerExecutionContext, T> hashFunction) {
        this.hashFunction = hashFunction;
        this.strategies = new ConcurrentHashMap<>();
    }

    public HashedHandlingStrategy<T> addStrategy(final @NonNull T key,
            final @NonNull TriggerHandlingStrategy strategy) {
        strategies.put(key, strategy);
        return this;
    }

    public HashedHandlingStrategy<T> removeStrategy(final @NonNull T key) {
        strategies.remove(key);
        return this;
    }

    @Override
    protected void doStart() {
        for (TriggerHandlingStrategy strategy : strategies.values())
            strategy.start();
    }

    @Override
    protected void doShutdown() {
        for (TriggerHandlingStrategy strategy : strategies.values())
            strategy.shutdown();
    }

    @Override
    public void handle(TriggerExecutionContext context) {
        TriggerHandlingStrategy strategy = findStrategy(context);
        if (strategy == null) {
            context.fail(new TriggerExecutionException(
                    new NoMatchingRouteException("No matching strategy for this execution context")));
            return;
        }
        strategy.handle(context);
    }

    private TriggerHandlingStrategy findStrategy(TriggerExecutionContext context) {
        Object hashValue = hashFunction.apply(context);
        return strategies.get(hashValue);
    }
}