package org.joo.scorpius.support;

import java.util.Optional;

public interface Traceable {

    public String getTraceId();

    public Optional<String> fetchRawTraceId();

    public void attachTraceId(Optional<String> traceId);

    public boolean verifyTraceId();
}
