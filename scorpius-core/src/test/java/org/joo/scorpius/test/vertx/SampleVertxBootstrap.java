package org.joo.scorpius.test.vertx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joo.scorpius.support.message.ExecutionContextExceptionMessage;
import org.joo.scorpius.support.vertx.VertxBootstrap;
import org.joo.scorpius.test.support.SampleTrigger;
import org.joo.scorpius.trigger.TriggerEvent;
import org.joo.scorpius.trigger.handle.disruptor.DisruptorHandlingStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lmax.disruptor.YieldingWaitStrategy;

import io.vertx.core.VertxOptions;

public class SampleVertxBootstrap extends VertxBootstrap {

    private final static Logger logger = LogManager.getLogger(SampleVertxBootstrap.class);

    private ObjectMapper mapper = new ObjectMapper();

    public void run() {
        configureTriggers();

        VertxOptions options = new VertxOptions().setEventLoopPoolSize(8);
        configureServer(options, 8080);
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
//        triggerManager.registerPeriodicEvent(new PeriodicTaskMessage(1000, 1000, new SampleRequest()))
//                .withAction(PeriodicTrigger::new);
    }
}
