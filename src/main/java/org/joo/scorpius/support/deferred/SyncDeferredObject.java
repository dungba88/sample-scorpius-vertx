package org.joo.scorpius.support.deferred;

@Deprecated
public class SyncDeferredObject<D, F extends Throwable> implements Deferred<D, F>, Promise<D, F> {

    private D result;

    private F failedCause;

    private volatile DeferredStatus status;

    private DoneCallback<D> doneCallback;

    private FailCallback<F> failCallback;

    @Override
    public Deferred<D, F> resolve(final D resolve) {
        synchronized (this) {
            if (!isPending())
                throw new IllegalStateException("Deferred object already finished, cannot resolve again");

            this.status = DeferredStatus.RESOLVED;
            this.result = resolve;
            triggerDone(doneCallback, resolve);
        }
        return this;
    }

    @Override
    public Deferred<D, F> reject(final F reject) {
        synchronized (this) {
            if (!isPending())
                throw new IllegalStateException("Deferred object already finished, cannot reject again");
            this.status = DeferredStatus.REJECTED;
            this.failedCause = reject;
            triggerFail(failCallback, reject);
        }
        return this;
    }

    @Override
    public Promise<D, F> done(DoneCallback<D> callback) {
        synchronized (this) {
            if (isResolved()) {
                triggerDone(callback, result);
            } else {
                doneCallback = callback;
            }
        }
        return this;
    }

    @Override
    public Promise<D, F> fail(FailCallback<F> callback) {
        synchronized (this) {
            if (isRejected()) {
                triggerFail(callback, failedCause);
            } else {
                failCallback = callback;
            }
        }
        return this;
    }

    private void triggerDone(DoneCallback<D> callback, D resolve) {
        if (callback != null) {
            callback.onDone(resolve);
        }
    }

    private void triggerFail(FailCallback<F> callback, F reject) {
        if (callback != null) {
            callback.onFail(reject);
        }
    }

    public Promise<D, F> promise() {
        return this;
    }

    public boolean isPending() {
        return status == null;
    }

    public boolean isResolved() {
        return status == DeferredStatus.RESOLVED;
    }

    public boolean isRejected() {
        return status == DeferredStatus.REJECTED;
    }
}