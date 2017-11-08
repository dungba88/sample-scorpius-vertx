package org.joo.scorpius.test.perf;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerManager;

public abstract class AbstractTriggerTest {

	protected long iterations = 0;
	
	protected TriggerManager manager;
	
	private ApplicationContext context;
	
	public AbstractTriggerTest(long iterations) {
		this.context = new ApplicationContext();
		this.manager = new TriggerManager(context);
		this.iterations = iterations;
	}
	
	public void test() {
		setup();

		warmup();

		long start = System.currentTimeMillis();
		
		doTest();
		
		long elapsed = System.currentTimeMillis() - start;
		long pace = iterations * 1000 / elapsed;
		
		System.out.println("Elapsed: " + elapsed + "ms");
		System.out.println("Pace: " + pace + " ops/sec");
		
		cleanup();
	}
	
	protected void setup() {
		this.manager.registerTrigger("greet", new TriggerConfig(new SampleTrigger()));
	}

	protected abstract void warmup();

	protected abstract void doTest();

	protected abstract void cleanup();
}
