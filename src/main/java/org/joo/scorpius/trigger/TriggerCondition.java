package org.joo.scorpius.trigger;

import org.joo.libra.support.PredicateExecutionException;

public interface TriggerCondition {

    public boolean satisfiedBy(TriggerExecutionContext executionContext) throws PredicateExecutionException;
}
