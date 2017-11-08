package org.joo.scorpius.support.deferred;

public interface DeferredFactory<D, F extends Throwable> {
	
	public Deferred<D, F> createDeferred();
}