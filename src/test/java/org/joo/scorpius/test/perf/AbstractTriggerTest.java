package org.joo.scorpius.test.perf;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.test.support.GroovyTrigger;
import org.joo.scorpius.test.support.SampleRequest;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.test.support.ScalaTrigger;
import org.joo.scorpius.trigger.DefaultTriggerManager;
import org.joo.scorpius.trigger.TriggerManager;

public abstract class AbstractTriggerTest {

	protected long iterations = 0;
	
	protected TriggerManager manager;
	
	private ApplicationContext context;

	public AbstractTriggerTest(long iterations) {
		this.context = new ApplicationContextBuilder().build();
		this.manager = new DefaultTriggerManager(context);
		this.iterations = iterations;
	}
	
	public void test() {
		try {
			System.out.println("Setting up...");
			setup();
	
			System.out.println("Warming up...");
			warmup();
			
			testInternal("greet_java");
			testInternal("greet_scala");
			testInternal("greet_groovy");
		} finally {
			System.out.println("\nCleaning up...");
			cleanup();
			System.out.println("Finished");
		}
	}
	
	private void testInternal(String msgName) {
		System.out.println("\nTesting (" + msgName + ")...");
		
		long start = System.currentTimeMillis();
		doTest(msgName);
		long elapsed = System.currentTimeMillis() - start;
		long pace = iterations * 1000 / elapsed;
		
		System.out.println("Elapsed: " + elapsed + "ms");
		System.out.println("Pace: " + pace + " ops/sec");
	}
	
	protected void setup() {
		manager.registerTrigger("greet_java").withAction(new SampleTrigger());
		manager.registerTrigger("greet_scala").withAction(new ScalaTrigger());
		manager.registerTrigger("greet_groovy").withAction(new GroovyTrigger());
	}

	protected void warmup() {
		for(int i=0; i<1000; i++) {
			manager.fire("greet_java", new SampleRequest());
			manager.fire("greet_scala", new SampleRequest());
			manager.fire("greet_groovy", new SampleRequest());
		}
	}

	protected abstract void doTest(String msgName);

	protected abstract void cleanup();
}
