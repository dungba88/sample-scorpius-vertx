package org.joo.scorpius.test.perf;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.DefaultTriggerManager;
import org.joo.scorpius.trigger.TriggerManager;

public abstract class AbstractTriggerTest {

	protected long iterations = 0;
	
	protected TriggerManager manager;
	
	private ApplicationContext context;
	
	public AbstractTriggerTest(long iterations) {
		this.context = new ApplicationContext();
		this.manager = new DefaultTriggerManager(context);
		this.iterations = iterations;
	}
	
	public void test() {
		try {
			System.out.println("Setting up...");
			setup();
	
			System.out.println("Warming up...");
			warmup();
			
			System.out.println("\nTesting...");
	
			long start = System.currentTimeMillis();
			doTest();
			long elapsed = System.currentTimeMillis() - start;
			long pace = iterations * 1000 / elapsed;
			
			System.out.println("Elapsed: " + elapsed + "ms");
			System.out.println("Pace: " + pace + " ops/sec");
		} finally {
			System.out.println("\nCleaning up...");
			cleanup();
			System.out.println("Finished");
		}
	}
	
	protected void setup() {
		this.manager.registerTrigger("greet").withAction(new SampleTrigger());
	}

	protected abstract void warmup();

	protected abstract void doTest();

	protected abstract void cleanup();
}
