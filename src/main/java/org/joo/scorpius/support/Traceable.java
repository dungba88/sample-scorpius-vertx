package org.joo.scorpius.support;

public interface Traceable {

	public String getTraceId();
	
	public void attachTraceId(String traceId);
	
	public boolean verifyTraceId();
}
