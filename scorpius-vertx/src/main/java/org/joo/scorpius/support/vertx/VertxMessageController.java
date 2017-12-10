package org.joo.scorpius.support.vertx;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.trigger.TriggerManager;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class VertxMessageController extends AbstractVertxController {

	public VertxMessageController(final TriggerManager triggerManager) {
		super(triggerManager);
	}

	public void handle(final RoutingContext rc) {
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
		
		doFireEvent(rc, msgName, request);
	}
}
