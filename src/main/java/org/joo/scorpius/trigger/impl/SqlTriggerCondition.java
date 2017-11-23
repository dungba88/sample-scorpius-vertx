package org.joo.scorpius.trigger.impl;

import org.joo.libra.PredicateContext;
import org.joo.libra.sql.SqlPredicate;
import org.joo.libra.support.PredicateExecutionException;
import org.joo.scorpius.trigger.TriggerCondition;
import org.joo.scorpius.trigger.TriggerExecutionContext;

public class SqlTriggerCondition implements TriggerCondition {

    private SqlPredicate predicate;

    public SqlTriggerCondition(String condition) {
        this.predicate = new SqlPredicate(condition);
        this.predicate.checkForErrorAndThrow();
    }

    @Override
    public boolean satisfiedBy(TriggerExecutionContext executionContext) {
        try {
            return this.predicate.satisfiedBy(new PredicateContext(executionContext));
        } catch (PredicateExecutionException e) {
            // TODO log error
            return false;
        }
    }
}
