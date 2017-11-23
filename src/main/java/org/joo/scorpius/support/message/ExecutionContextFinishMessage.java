package org.joo.scorpius.support.message;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;

public class ExecutionContextFinishMessage extends ExecutionContextStartMessage {

    private static final long serialVersionUID = -3920788121935515927L;

    private final BaseResponse response;

    public ExecutionContextFinishMessage(String id, String eventName, BaseRequest request, BaseResponse response) {
        super(id, eventName, request);
        this.response = response;
    }

    public BaseResponse getResponse() {
        return response;
    }
}
