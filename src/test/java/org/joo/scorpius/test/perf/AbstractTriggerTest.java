package org.joo.scorpius.test.perf;

import java.util.ArrayList;
import java.util.List;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.builders.ApplicationContextBuilder;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.test.support.ScalaTrigger;
import org.joo.scorpius.trigger.DefaultTriggerManager;
import org.joo.scorpius.trigger.TriggerManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractTriggerTest {

	protected long iterations = 0;
	
	protected TriggerManager manager;
	
	private ApplicationContext context;

	@Parameters
	public static List<Object[]> data() {
		List<Object[]> list = new ArrayList<>();
		list.add(new Object[] {1000});
		list.add(new Object[] {10000});
		list.add(new Object[] {100000});
		list.add(new Object[] {1000000});
		list.add(new Object[] {10000000});
		return list;
	}

	public AbstractTriggerTest(long iterations) {
		this.context = new ApplicationContextBuilder().build();
		this.manager = new DefaultTriggerManager(context);
		this.iterations = iterations;
	}
	
	@Test
	public void test() {
		try {
			System.out.println("Setting up...");
			setup();
	
			System.out.println("Warming up...");
			warmup();
			
			testInternal("greet_java");
			testInternal("greet_scala");
		} finally {
			System.out.println("\nCleaning up...");
			cleanup();
			System.out.println("Finished");
		}
	}
	
	private void testInternal(String msgName) {
		System.out.println("\nTesting (" + msgName + ")...");
		
		long start = System.currentTimeMillis();
		doTest(iterations, msgName);
		long elapsed = System.currentTimeMillis() - start;
		long pace = elapsed != 0 ? iterations * 1000 / elapsed : -1;
		
		System.out.println("Elapsed: " + elapsed + "ms");
		System.out.println("Pace: " + pace + " ops/sec");
	}
	
	protected void setup() {
		manager.registerTrigger("greet_java").withAction(new SampleTrigger());
		manager.registerTrigger("greet_scala").withAction(new ScalaTrigger());
	}

	protected void warmup() {
		doTest(1000, "greet_java");
		doTest(1000, "greet_scala");
	}

	protected abstract void doTest(long iterations, String msgName);

	protected abstract void cleanup();
}
