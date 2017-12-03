package org.joo.scorpius.support.config;

import org.joo.scorpius.support.bootstrap.AbstractBootstrap;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class TypeSafeBootstrap extends AbstractBootstrap {
    
    private Config config;

    public TypeSafeBootstrap() {
        this.config = ConfigFactory.load();
    }
    
    public TypeSafeBootstrap(String configFileName) {
        this.config = ConfigFactory.load(configFileName);
    }
    
    public TypeSafeBootstrap(Config config) {
        this.config = ConfigFactory.load(config);
    }

    @Override
    public void run() {
        config.checkValid(ConfigFactory.defaultReference());
        applicationContext.override(Config.class, config);
    }
}
