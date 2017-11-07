package org.joo.scorpius.test.vertx;

import io.vertx.core.Vertx;

public class VertxApplicationContext {

	private Vertx vertx;
	
	public VertxApplicationContext(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public Vertx getVertx() {
		return vertx;
	}
}
