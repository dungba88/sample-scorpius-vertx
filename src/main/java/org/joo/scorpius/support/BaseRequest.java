package org.joo.scorpius.support;

import java.io.Serializable;
import java.util.Optional;

public class BaseRequest implements Traceable, Serializable {

    private static final long serialVersionUID = 2115391882186882706L;

    private transient Optional<String> traceId;

    public BaseRequest() {

    }

    public BaseRequest(final Optional<String> traceId) {
        attachTraceId(traceId);
    }

    @Override
    public void attachTraceId(final Optional<String> traceId) {
        if (this.traceId != null)
            throw new IllegalStateException("TraceId is already attached");
        this.traceId = traceId;
    }

    @Override
    public boolean verifyTraceId() {
        if (traceId == null)
            return false;
        if (!traceId.isPresent())
            return true;
        return !traceId.get().isEmpty();
    }

    @Override
    public String getTraceId() {
        return traceId != null ? traceId.orElse(null) : null;
    }

    @Override
    public Optional<String> fetchRawTraceId() {
        return traceId;
    }
}
