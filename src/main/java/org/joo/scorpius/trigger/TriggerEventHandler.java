package org.joo.scorpius.trigger;

public interface TriggerEventHandler {

	public void handleEvent(TriggerEvent event, Object msg);
}
