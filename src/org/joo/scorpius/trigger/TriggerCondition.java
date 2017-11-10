package org.joo.scorpius.trigger;

public interface TriggerCondition {

	public boolean satisfiedBy(TriggerExecutionContext executionContext);
}
