package org.joo.scorpius.support.typesafe;

import java.util.List;
import java.util.Objects;

import org.joo.promise4j.Promise;
import org.joo.promise4j.impl.SimpleDonePromise;
import org.joo.scorpius.support.bootstrap.AbstractBootstrap;
import org.joo.scorpius.support.exception.BootstrapInitializationException;
import org.joo.scorpius.trigger.Trigger;
import org.joo.scorpius.trigger.TriggerConfig;

import com.typesafe.config.Config;

import lombok.Getter;

public class TriggerTypeSafeBootstrap extends AbstractBootstrap {

    private static final String DEFAULT_CONFIG_NAME = "triggers";

    private String configName;

    public TriggerTypeSafeBootstrap() {
        this(DEFAULT_CONFIG_NAME);
    }

    public TriggerTypeSafeBootstrap(String configName) {
        this.configName = configName;
    }

    @Override
    public Promise<?, Throwable> run() {
        Config config = applicationContext.getInstance(Config.class);
        if (config == null) {
            throw new IllegalStateException(
                    "TypeSafeBootstrap is not initialized. Please make sure it is initialized before this");
        }

        List<? extends Config> configList = config.getConfigList(configName);

        configList.stream().map(this::parseTriggerConfig).filter(Objects::nonNull)
                .forEach(cfg -> triggerManager.registerTrigger(cfg.getEvent(), cfg.getConfig()));
        return new SimpleDonePromise<>(null);
    }
    
    private TriggerConfigWrapper parseTriggerConfig(Config cfg) {
        String condition = cfg.hasPath("condition") ? cfg.getString("condition") : null;
        String action = cfg.getString("action");

        TriggerConfig config;
        try {
            config = parseTriggerConfig(condition, action);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new BootstrapInitializationException(e);
        }

        return new TriggerConfigWrapper(cfg.getString("event"), config);
    }
    
    @SuppressWarnings("unchecked")
    private TriggerConfig parseTriggerConfig(String condition, String action)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        TriggerConfig config = new TriggerConfig();
        if (condition != null)
            config.withCondition(condition);
        Class<Trigger<?, ?>> clazz = (Class<Trigger<?, ?>>) Class.forName(action);
        config.withAction(clazz.newInstance());
        return config;
    }
}

class TriggerConfigWrapper {

    private final @Getter String event;

    private final @Getter TriggerConfig config;

    public TriggerConfigWrapper(final String event, final TriggerConfig config) {
        this.event = event;
        this.config = config;
    }
}
