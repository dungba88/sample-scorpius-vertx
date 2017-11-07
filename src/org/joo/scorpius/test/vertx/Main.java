package org.joo.scorpius.test.vertx;

import org.joo.scorpius.test.vertx.sample.SampleVertxBootstrap;

public class Main {

	public static void main(String[] args) {
		Application app = new Application();
		app.run(new SampleVertxBootstrap());
	}
}
