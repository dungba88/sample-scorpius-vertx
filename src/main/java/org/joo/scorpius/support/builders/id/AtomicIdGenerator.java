package org.joo.scorpius.support.builders.id;

import java.util.concurrent.atomic.AtomicReference;

import org.joo.scorpius.support.builders.Factory;

public class AtomicIdGenerator implements Factory<String> {
	
	private AtomicReference<AtomicCounter> counter;
	
	public AtomicIdGenerator() {
		counter = new AtomicReference<AtomicCounter>(new AtomicCounter(System.currentTimeMillis(), 0));
	}
	
	@Override
	public String create() {
		AtomicCounter currentCounter;
		AtomicCounter nextCounter;
		do {
			currentCounter = counter.get();
			nextCounter = currentCounter.next();
		} while(!counter.compareAndSet(currentCounter, nextCounter));
		return nextCounter.toString();
	}
}

class AtomicCounter {

	private long prefix;
	
	private long counter;
	
	public AtomicCounter(long prefix, long counter) {
		this.prefix = prefix;
		this.counter = counter;
	}
	
	public AtomicCounter next() {
		if (++counter == Long.MIN_VALUE) {
			prefix = System.currentTimeMillis();
			counter = 0;
		}
		return new AtomicCounter(prefix, counter);
	}
	
	@Override
	public String toString() {
		return prefix + ":" + counter;
	}
}