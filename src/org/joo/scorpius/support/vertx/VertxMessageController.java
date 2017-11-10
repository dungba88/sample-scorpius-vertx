package org.joo.scorpius.support.vertx;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.MalformedRequestException;
import org.joo.scorpius.trigger.TriggerManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class VertxMessageController implements Handler<RoutingContext> {

	private TriggerManager triggerManager;

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
		}

		triggerManager.fire(msgName, request).done(triggerResponse -> {
			onDone(triggerResponse, response, rc);
		}).fail(exception -> {
			onFail(exception, response, rc);
		});
	}

	private void onFail(Throwable exception, HttpServerResponse response, RoutingContext rc) {
		rc.fail(exception);
	}

	private void onDone(BaseResponse triggerResponse, HttpServerResponse response, RoutingContext rc) {
		if (triggerResponse == null) {
			response.end();
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			String strResponse = mapper.writeValueAsString(triggerResponse);
			response.end(strResponse);
		} catch (JsonProcessingException e) {
			rc.fail(e);
		}
	}
}
