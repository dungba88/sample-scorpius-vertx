package org.joo.scorpius.trigger;

import java.io.Serializable;

public interface TriggerEventDispatcher {

	public void addEventHandler(TriggerEvent event, TriggerEventHandler handler);

	public void notifyEvent(TriggerEvent event, Serializable msg);
	
	public boolean isEventEnabled(TriggerEvent start);
}
