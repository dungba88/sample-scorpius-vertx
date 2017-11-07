package org.joo.scorpius.test.vertx;

import org.joo.scorpius.test.vertx.trigger.TriggerManager;

public interface Bootstrap {
	
	public void setTriggerManager(TriggerManager triggerManager);
	
	public void setApplicationContext(VertxApplicationContext applicationContext);

	public void run();
}
