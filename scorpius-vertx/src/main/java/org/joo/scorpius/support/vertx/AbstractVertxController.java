package org.joo.scorpius.support.vertx;

import java.util.Optional;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.CommonConstants;
import org.joo.scorpius.support.builders.contracts.IdGenerator;
import org.joo.scorpius.trigger.TriggerManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public abstract class AbstractVertxController implements Handler<RoutingContext> {

    private final ObjectMapper mapper;

    protected final TriggerManager triggerManager;

    public AbstractVertxController(final TriggerManager triggerManager) {
        this.triggerManager = triggerManager;
        ObjectMapper configuredMapper = this.triggerManager.getApplicationContext().getInstance(ObjectMapper.class);
        this.mapper = configuredMapper != null ? configuredMapper : new ObjectMapper();
    }

    protected void doFireEvent(RoutingContext rc, String msgName, BaseRequest request) {
        if (request != null && request.fetchRawTraceId() != null)
            request.attachTraceId(getTraceId(rc, triggerManager.getApplicationContext()));

        triggerManager.fire(msgName, request).done(triggerResponse -> onDone(triggerResponse, rc.response(), rc))
                .fail(exception -> onFail(exception, rc.response(), rc));
    }

    protected Optional<String> getTraceId(final RoutingContext rc, final ApplicationContext applicationContext) {
        String traceId = rc.request().getHeader(CommonConstants.TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty())
            return applicationContext.getInstance(IdGenerator.class).create();
        return Optional.of(traceId);
    }

    protected void onFail(final Throwable exception, final HttpServerResponse response, final RoutingContext rc) {
        rc.fail(exception);
    }

    protected void onDone(final BaseResponse triggerResponse, final HttpServerResponse response,
            final RoutingContext rc) {
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
