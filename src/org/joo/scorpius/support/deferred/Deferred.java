package org.joo.scorpius.support.deferred;

public interface Deferred<D, F extends Throwable> {

	public void resolve(D result);
	
	public void reject(F failedCause);
	
	public Promise<D, F> promise();
}
