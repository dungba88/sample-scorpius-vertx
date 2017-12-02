package org.joo.scorpius.trigger.impl;

import org.joo.libra.PredicateContext;
import org.joo.libra.sql.SqlPredicate;
import org.joo.libra.support.PredicateExecutionException;
import org.joo.scorpius.trigger.TriggerCondition;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SqlTriggerCondition implements TriggerCondition {

    private SqlPredicate predicate;

    public SqlTriggerCondition(final String condition) {
        this.predicate = new SqlPredicate(condition);
        this.predicate.checkForErrorAndThrow();
    }

    @Override
    public boolean satisfiedBy(final TriggerExecutionContext executionContext) throws PredicateExecutionException {
        return this.predicate.satisfiedBy(new PredicateContext(executionContext));
    }
}
