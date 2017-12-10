package org.joo.scorpius.support.vertx;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joo.scorpius.support.bootstrap.AbstractBootstrap;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class VertxBootstrap extends AbstractBootstrap {

	private static final Logger logger = LogManager.getLogger(VertxBootstrap.class);

	private static final String DEFAULT_ENDPOINT = "/msg";

	private VertxMessageController msgController;

	private Vertx vertx;

	private HttpServer server;

	private String endpoint;

	private int port;

	private Function<Vertx, Router> routingConfig;
	
	public VertxBootstrap(final VertxOptions vertxOptions, final int port) {
		this(vertxOptions, new HttpServerOptions(), port);
	}

	public VertxBootstrap(final VertxOptions vertxOptions, final HttpServerOptions httpOptions, final int port) {
		this(vertxOptions, httpOptions, port, DEFAULT_ENDPOINT);
	}

	public VertxBootstrap(final VertxOptions vertxOptions, final HttpServerOptions httpOptions, final int port,
			String endpoint) {
		this.vertx = Vertx.vertx(vertxOptions);
		this.server = vertx.createHttpServer(httpOptions);
		this.port = port;
		this.endpoint = endpoint;
	}
	
	public VertxBootstrap withRoutingConfig(Function<Vertx, Router> routingConfig) {
		this.routingConfig = routingConfig;
		return this;
	}

	public void run() {
		msgController = new VertxMessageController(triggerManager);
		Router restAPI = routingConfig != null ? routingConfig.apply(vertx) : configureRoutes(vertx);
		server.requestHandler(restAPI::accept).listen(port, res -> {
			if (res.failed()) {
				if (logger.isFatalEnabled())
					logger.fatal("Exception occurred while initializing Vertx Web", res.cause());
				server.close();
			} else if (logger.isInfoEnabled()) {
				logger.info("Vertx Web started listening at port " + port);
			}
		});
	}

	protected Router configureRoutes(final Vertx vertx) {
		Router restAPI = Router.router(vertx);
		restAPI.post("/*").handler(BodyHandler.create());
		restAPI.post(endpoint).handler(msgController::handle);
		return restAPI;
	}
}
