package org.joo.scorpius.support;

import java.util.Optional;

public interface Traceable {

	public Optional<String> getTraceId();
	
	public void attachTraceId(Optional<String> traceId);
	
	public boolean verifyTraceId();
}
