package org.joo.scorpius.support.vertx;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.trigger.TriggerManager;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public abstract class VertxBootstrap implements Bootstrap {

    protected ApplicationContext applicationContext;

    protected TriggerManager triggerManager;

    protected VertxMessageController msgController;

    protected void configureServer(VertxOptions options, int port) {
        msgController = new VertxMessageController(triggerManager);

        Vertx vertx = Vertx.vertx(options);
        HttpServer server = vertx.createHttpServer();

        Router restAPI = configureRoutes(vertx);

        server.requestHandler(restAPI::accept).listen(port);
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
