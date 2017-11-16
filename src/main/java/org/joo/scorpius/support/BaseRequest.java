package org.joo.scorpius.support;

import java.io.Serializable;

public class BaseRequest implements Traceable, Serializable {

	private static final long serialVersionUID = 2115391882186882706L;
	
	private String traceId;
	
	public BaseRequest() {
		
	}
	
	public BaseRequest(String traceId) {
		this.traceId = traceId;
	}
	
	@Override
	public void attachTraceId(String traceId) {
		if (this.traceId != null)
			throw new IllegalStateException("TraceId is already attached");
		this.traceId  = traceId;
	}
	
	@Override
	public String getTraceId() {
		return traceId;
	}

	@Override
	public boolean verifyTraceId() {
		return traceId != null && !traceId.isEmpty();
	}
}
