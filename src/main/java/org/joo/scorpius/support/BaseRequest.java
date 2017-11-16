package org.joo.scorpius.support;

import java.io.Serializable;
import java.util.Optional;

public class BaseRequest implements Traceable, Serializable {

	private static final long serialVersionUID = 2115391882186882706L;
	
	private Optional<String> traceId;
	
	public BaseRequest() {
		
	}
	
	public BaseRequest(Optional<String> traceId) {
		attachTraceId(traceId);
	}
	
	@Override
	public void attachTraceId(Optional<String> traceId) {
		if (this.traceId != null)
			throw new IllegalStateException("TraceId is already attached");
		this.traceId  = traceId;
	}
	
	@Override
	public Optional<String> getTraceId() {
		return traceId;
	}

	@Override
	public boolean verifyTraceId() {
		if (traceId == null || !traceId.isPresent()) return true;
		return !traceId.get().isEmpty();
	}
}
