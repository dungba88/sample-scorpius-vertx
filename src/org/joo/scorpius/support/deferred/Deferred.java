package org.joo.scorpius.support.deferred;

public interface Deferred<D, F extends Throwable> {

	public Deferred<D, F> resolve(D result);
	
	public Deferred<D, F> reject(F failedCause);
	
	public Promise<D, F> promise();
}
