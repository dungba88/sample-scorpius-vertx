package org.joo.scorpius.test.vertx;

import org.joo.scorpius.Application;

public class Main {

    public static void main(String[] args) {
        Application app = new Application();
        app.run(new SampleVertxBootstrap());
    }
}
