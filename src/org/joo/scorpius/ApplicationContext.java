package org.joo.scorpius;

import org.joo.scorpius.support.deferred.DeferredFactory;
import org.joo.scorpius.support.deferred.SyncDeferredObject;

public class ApplicationContext {

	private DeferredFactory deferredFactory;
	
	public ApplicationContext() {
		deferredFactory = () -> new SyncDeferredObject<>();
	}

	public DeferredFactory getDeferredFactory() {
		return deferredFactory;
	}

	public void setDeferredFactory(DeferredFactory deferredFactory) {
		this.deferredFactory = deferredFactory;
	}
}
