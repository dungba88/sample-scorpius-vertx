package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseRequest;

public class TraceIdRequiredRequest extends BaseRequest {

    private static final long serialVersionUID = 6047040583668858489L;

    private String name;

    public TraceIdRequiredRequest() {

    }

    public TraceIdRequiredRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
