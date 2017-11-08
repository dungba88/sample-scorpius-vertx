package org.joo.scorpius.support.deferred;

public interface Promise<D, F extends Throwable> {

	public Promise<D, F> done(DoneCallback<D> callback);
	
	public Promise<D, F> fail(FailureCallback<F> callback);
}
