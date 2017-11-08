package org.joo.scorpius.support.deferred;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

public class SimpleDonePromise<D, F extends Throwable> implements Promise<D, F> {
	
	private D result;
	
	public SimpleDonePromise(D result) {
		this.result = result;
	}
	
	@Override
	public Promise<D, F> done(DoneCallback<D> callback) {
		callback.onDone(result);
		return this;
	}

	@Override
	public Promise<D, F> fail(FailCallback<F> callback) {
		return this;
	}
}
