package org.joo.scorpius.support.deferred;

import java.util.concurrent.atomic.AtomicBoolean;

public class DeferredObject<D, F extends Throwable> implements Deferred<D, F>, Promise<D, F> {
	
	private volatile D result;
	
	private volatile F failedCause;
	
	private volatile DeferredStatus status;
	
	private volatile DoneCallback<D> doneCallback;
	
	private volatile FailureCallback<F> failureCallback;

	private AtomicBoolean done;
	
	public DeferredObject() {
		this.done = new AtomicBoolean(false);
	}
	
	@Override
	public void resolve(D result) {
		if (!done.compareAndSet(false, true))
			throw new IllegalAccessError("Deferred is already resolved or rejected");
		this.result = result;
		this.status = DeferredStatus.DONE;
		this.onComplete(result);
	}
	
	@Override
	public void reject(F failedCause) {
		if (!done.compareAndSet(false, true))
			throw new IllegalAccessError("Deferred is already resolved or rejected");
		this.failedCause = failedCause;
		this.status = DeferredStatus.REJECTED;
		this.onFail(failedCause);
	}

	private void onComplete(D result) {
		if (doneCallback != null)
			doneCallback.onDone(result);
	}

	private void onFail(F failedCause) {
		if (failureCallback != null)
			failureCallback.onFail(failedCause);
	}
	
	@Override
	public Promise<D, F> promise() {
		return this;
	}

	@Override
	public Promise<D, F> done(DoneCallback<D> callback) {
		if (status == DeferredStatus.DONE) {
			callback.onDone(result);
		} else {
			this.doneCallback = callback;
		}
		return this;
	}

	@Override
	public Promise<D, F> fail(FailureCallback<F> callback) {
		if (status == DeferredStatus.REJECTED) {
			callback.onFail(failedCause);
		} else {
			this.failureCallback = callback;
		}
		return this;
	}
}

enum DeferredStatus {
	DONE, REJECTED;
}