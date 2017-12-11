package org.joo.scorpius.support.vertx.verticle;

import java.util.function.BiFunction;

import org.joo.scorpius.Application;
import org.joo.scorpius.Bootstrap;

import io.vertx.core.Vertx;

public class SimpleScorpiusVerticle extends AbstractScorpiusVerticle {

    private BiFunction<Application, Vertx, Bootstrap<?>> config;

    public SimpleScorpiusVerticle withBootstrapConfig(BiFunction<Application, Vertx, Bootstrap<?>> config) {
        this.config = config;
        return this;
    }

    @Override
    protected Bootstrap<?> configureBootstraps(Application application, Vertx vertx) {
        if (config == null)
            throw new IllegalArgumentException("Bootstrap must be configured using withBootstrapConfig()");
        return config.apply(application, vertx);
    }
}
