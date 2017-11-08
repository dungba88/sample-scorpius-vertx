package org.joo.scorpius.test.vertx;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.support.deferred.AsyncDeferredObject;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.TriggerConfig;
import org.joo.scorpius.trigger.TriggerManager;
import org.joo.scorpius.trigger.handle.DefaultHandlingStrategy;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class SampleVertxBootstrap implements Bootstrap {
	
	private ApplicationContext applicationContext;
	
	private VertxMessageController msgController;

	private TriggerManager triggerManager;
	
	public void run() {
		configuredDeferredFactory();
		configureTriggers();
		configureServer();
	}

	private void configuredDeferredFactory() {
		applicationContext.setDeferredFactory(() -> new AsyncDeferredObject<>());
	}

	private void configureTriggers() {
		triggerManager.setHandlingStategy(new DefaultHandlingStrategy());
		triggerManager.registerTrigger("greet", new TriggerConfig(new SampleTrigger()));
	}

	private void configureServer() {
		msgController = new VertxMessageController(triggerManager);

		Vertx vertx = Vertx.vertx();
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
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
