package org.joo.scorpius.support.vertx;

import java.util.Optional;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.CommonConstants;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.trigger.TriggerManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class VertxMessageController implements Handler<RoutingContext> {

    private final static ObjectMapper mapper = new ObjectMapper();

    protected final TriggerManager triggerManager;

    public VertxMessageController(TriggerManager triggerManager) {
        this.triggerManager = triggerManager;
    }

    public void handle(RoutingContext rc) {
        HttpServerResponse response = rc.response();
        response.putHeader("Content-Type", "application/json");

        String msgName = rc.request().getParam("name");
        String msgData = rc.getBodyAsString();

        BaseRequest request = null;

        try {
            request = triggerManager.decodeRequestForEvent(msgName, msgData);
        } catch (MalformedRequestException e) {
            onFail(e, response, rc);
            return;
        }

        request.attachTraceId(getTraceId(rc, triggerManager.getApplicationContext()));

        triggerManager.fire(msgName, request).done(triggerResponse -> {
            onDone(triggerResponse, response, rc);
        }).fail(exception -> {
            onFail(exception, response, rc);
        });
    }

    protected Optional<String> getTraceId(RoutingContext rc, ApplicationContext applicationContext) {
        String traceId = rc.request().getHeader(CommonConstants.TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            return triggerManager.getApplicationContext().getInstance(IdGenerator.class).create();
        }
        return Optional.of(traceId);
    }

    protected void onFail(Throwable exception, HttpServerResponse response, RoutingContext rc) {
        rc.fail(exception);
    }

    protected void onDone(BaseResponse triggerResponse, HttpServerResponse response, RoutingContext rc) {
        if (triggerResponse == null) {
            response.end();
            return;
        }
        try {
            String strResponse = mapper.writeValueAsString(triggerResponse);
            response.end(strResponse);
        } catch (JsonProcessingException e) {
            rc.fail(e);
        }
    }
}
