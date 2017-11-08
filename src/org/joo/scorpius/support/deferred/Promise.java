package org.joo.scorpius.support.deferred;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

public interface Promise<D, F extends Throwable> {

	public Promise<D, F> done(DoneCallback<D> callback);
	
	public Promise<D, F> fail(FailCallback<F> callback);
}
