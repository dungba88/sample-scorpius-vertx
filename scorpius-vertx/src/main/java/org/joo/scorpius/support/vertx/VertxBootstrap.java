package org.joo.scorpius.support.vertx;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.trigger.TriggerManager;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Setter;

public abstract class VertxBootstrap implements Bootstrap {

    protected @Setter ApplicationContext applicationContext;

    protected @Setter TriggerManager triggerManager;

    protected VertxMessageController msgController;

    protected void configureServer(final VertxOptions options, final int port) {
        msgController = new VertxMessageController(triggerManager);

        Vertx vertx = Vertx.vertx(options);
        HttpServer server = vertx.createHttpServer();

        Router restAPI = configureRoutes(vertx);

        server.requestHandler(restAPI::accept).listen(port);
    }

    protected Router configureRoutes(final Vertx vertx) {
        Router restAPI = Router.router(vertx);
        restAPI.post("/*").handler(BodyHandler.create());
        restAPI.post("/msg").handler(msgController::handle);
        return restAPI;
    }
}
