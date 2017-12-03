package org.joo.scorpius.test.vertx;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joo.scorpius.Bootstrap;
import org.joo.scorpius.support.bootstrap.AbstractBootstrap;
import org.joo.scorpius.support.bootstrap.CompositionBootstrap;
import org.joo.scorpius.support.message.ExecutionContextExceptionMessage;
import org.joo.scorpius.support.vertx.VertxBootstrap;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.TriggerEvent;
import org.joo.scorpius.trigger.handle.disruptor.DisruptorHandlingStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.YieldingWaitStrategy;

import io.vertx.core.VertxOptions;

public class SampleVertxBootstrap extends CompositionBootstrap {

    private final static Logger logger = LogManager.getLogger(SampleVertxBootstrap.class);

    private ObjectMapper mapper = new ObjectMapper();

    protected void configureBootstraps(List<Bootstrap> bootstrap) {
        bootstrap.add(new VertxBootstrap(new VertxOptions().setEventLoopPoolSize(8), 8080));
        bootstrap.add(AbstractBootstrap.from(this::configureTriggers));
    }

    private void configureTriggers() {
        triggerManager.setHandlingStrategy(new DisruptorHandlingStrategy(1024, new YieldingWaitStrategy()));

        triggerManager.addEventHandler(TriggerEvent.EXCEPTION, (event, msg) -> {
            ExecutionContextExceptionMessage exceptionMsg = (ExecutionContextExceptionMessage) msg;
            try {
                logger.debug(mapper.writeValueAsString(exceptionMsg.getRequest()));
            } catch (JsonProcessingException e) {
            }
        });

        triggerManager.registerTrigger("greet_java").withAction(SampleTrigger::new);
    }
}
