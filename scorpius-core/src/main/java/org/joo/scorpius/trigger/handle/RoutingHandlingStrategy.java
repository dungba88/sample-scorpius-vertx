package org.joo.scorpius.trigger.handle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joo.libra.support.PredicateExecutionException;
import org.joo.scorpius.support.exception.NoMatchingRouteException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.trigger.TriggerCondition;
import org.joo.scorpius.trigger.TriggerExecutionContext;

import lombok.Getter;
import lombok.NonNull;

public class RoutingHandlingStrategy implements TriggerHandlingStrategy {

    private Map<String, StrategyRoute> routes = new ConcurrentHashMap<>();

    public RoutingHandlingStrategy addRoute(final @NonNull String name, final TriggerCondition condition,
            final @NonNull TriggerHandlingStrategy strategy) {
        routes.put(name, new StrategyRoute(condition, strategy));
        return this;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void handle(TriggerExecutionContext context) {
        try {
            StrategyRoute route = findRoute(context);
            if (route == null) {
                context.fail(new TriggerExecutionException(
                        new NoMatchingRouteException("No matching strategy for this execution context")));
                return;
            }
            route.getStrategy().handle(context);
        } catch (PredicateExecutionException e) {
            context.fail(new TriggerExecutionException(e));
        }
    }

    private StrategyRoute findRoute(TriggerExecutionContext context) throws PredicateExecutionException {
        for (StrategyRoute route : routes.values()) {
            if (route.getCondition() == null || route.getCondition().satisfiedBy(context))
                return route;
        }
        return null;
    }
}

@Getter
class StrategyRoute {

    private final TriggerCondition condition;

    private final TriggerHandlingStrategy strategy;

    public StrategyRoute(final TriggerCondition condition, final @NonNull TriggerHandlingStrategy strategy) {
        this.condition = condition;
        this.strategy = strategy;
    }
}