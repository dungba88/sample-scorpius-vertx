package org.joo.scorpius.support.deferred;

public class SimpleFailurePromise<D, F extends Throwable> implements Promise<D, F> {
	
	private F failedCause;
	
	public SimpleFailurePromise(F failedCause) {
		this.failedCause = failedCause;
	}
	
	@Override
	public Promise<D, F> done(DoneCallback<D> callback) {
		return this;
	}

	@Override
	public Promise<D, F> fail(FailCallback<F> callback) {
		callback.onFail(failedCause);
		return this;
	}
}
