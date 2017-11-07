package org.joo.scorpius.test.vertx.sample;

import org.joo.scorpius.test.vertx.Bootstrap;
import org.joo.scorpius.test.vertx.VertxApplicationContext;
import org.joo.scorpius.test.vertx.trigger.TriggerConfig;
import org.joo.scorpius.test.vertx.trigger.TriggerManager;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class SampleVertxBootstrap implements Bootstrap {
	
	private VertxApplicationContext applicationContext;
	
	private MessageController msgController;

	private TriggerManager triggerManager;
	
	public void run() {
		configureTriggers();
		configureServer();
	}

	private void configureTriggers() {
		triggerManager.registerTrigger("react.greetings", new TriggerConfig(new SampleTrigger()));
	}

	private void configureServer() {
		msgController = new MessageController(triggerManager);

		Vertx vertx = applicationContext.getVertx();
		HttpServer server = vertx.createHttpServer();
		
		Router restAPI = configureRoutes(vertx);
		
		server.requestHandler(restAPI::accept).listen(8080);
	}

	protected Router configureRoutes(Vertx vertx) {
		Router restAPI = Router.router(vertx);
		restAPI.post("/*").handler(BodyHandler.create());
		restAPI.post("/msg").handler(msgController::handle);
		return restAPI;
	}

	@Override
	public void setTriggerManager(TriggerManager triggerManager) {
		this.triggerManager = triggerManager;
	}

	@Override
	public void setApplicationContext(VertxApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
