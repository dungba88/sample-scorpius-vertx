package org.joo.scorpius.test.vertx;

import org.joo.scorpius.test.vertx.sample.SampleVertxBootstrap;

public class Main {

	public static void main(String[] args) {
		VertxApplication app = new VertxApplication();
		app.run(new SampleVertxBootstrap());
	}
}
