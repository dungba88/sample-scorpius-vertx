package org.joo.scorpius.support.deferred;

public interface FailureCallback<F extends Throwable> {

	public void onFail(final F failedCause);
}
