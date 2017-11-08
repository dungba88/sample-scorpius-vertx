package org.joo.scorpius.support.deferred;

public interface FailCallback<F extends Throwable> {

	public void onFail(final F failedCause);
}
