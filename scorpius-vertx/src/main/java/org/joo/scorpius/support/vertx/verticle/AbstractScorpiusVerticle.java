package org.joo.scorpius.support.vertx.verticle;

import org.joo.scorpius.Application;
import org.joo.scorpius.Bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import lombok.Getter;

public abstract class AbstractScorpiusVerticle extends AbstractVerticle {

    private @Getter Application application;

    public AbstractScorpiusVerticle() {

    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        application = new Application();

        application.run(configureBootstraps(application, vertx)).done(res -> {
            startFuture.complete();
        }).fail(ex -> {
            startFuture.fail(ex);
        });
    }

    @Override
    public void stop() throws Exception {
        application.shutdown();
    }

    protected abstract Bootstrap<?> configureBootstraps(Application application, Vertx vertx);
}
