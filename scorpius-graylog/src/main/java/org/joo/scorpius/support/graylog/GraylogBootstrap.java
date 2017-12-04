package org.joo.scorpius.support.graylog;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.bootstrap.AbstractBootstrap;
import org.joo.scorpius.support.graylog.msg.AnnotatedExecutionContextCreatedMessage;
import org.joo.scorpius.support.graylog.msg.AnnotatedExecutionContextExceptionMessage;
import org.joo.scorpius.support.graylog.msg.AnnotatedExecutionContextFinishMessage;
import org.joo.scorpius.support.graylog.msg.AnnotatedExecutionContextStartMessage;
import org.joo.scorpius.support.message.ExecutionContextExceptionMessage;
import org.joo.scorpius.support.message.ExecutionContextFinishMessage;
import org.joo.scorpius.support.message.ExecutionContextStartMessage;
import org.joo.scorpius.trigger.TriggerEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GraylogBootstrap extends AbstractBootstrap {

    static {
        PluginManager.addPackage(AnnotatedGelfJsonAppender.class.getPackage().getName());
    }

    private static final Logger logger = LogManager.getLogger(GraylogBootstrap.class);

    private Set<TriggerEvent> events;

    private ObjectMapper mapper;

    public GraylogBootstrap(ObjectMapper mapper) {
        this(mapper, TriggerEvent.EXCEPTION);
    }

    public GraylogBootstrap(ObjectMapper mapper, TriggerEvent... events) {
        this.mapper = mapper;
        this.events = new HashSet<>(Arrays.asList(events));
    }

    @Override
    public void run() {
        registerEventHandlers();
    }

    protected void registerEventHandlers() {
        if (events.contains(TriggerEvent.EXCEPTION))
            registerTriggerExceptionHandler(applicationContext);

        if (events.contains(TriggerEvent.CREATED))
            registerTriggerCreateHandler();

        if (events.contains(TriggerEvent.START))
            registerTriggerStartHandler();

        if (events.contains(TriggerEvent.FINISH))
            registerTriggerFinishHandler();
    }

    protected void registerTriggerExceptionHandler(ApplicationContext applicationContext) {
        triggerManager.addEventHandler(TriggerEvent.EXCEPTION, (event, msg) -> {
            ExecutionContextExceptionMessage exceptionMessage = (ExecutionContextExceptionMessage) msg;
            if (logger.isErrorEnabled())
                logger.error(new AnnotatedExecutionContextExceptionMessage(mapper, exceptionMessage));
        });
    }

    protected void registerTriggerCreateHandler() {
        triggerManager.addEventHandler(TriggerEvent.CREATED, (event, msg) -> {
            ExecutionContextStartMessage startMessage = (ExecutionContextStartMessage) msg;
            if (logger.isDebugEnabled())
                logger.debug(new AnnotatedExecutionContextCreatedMessage(mapper, startMessage));
        });
    }

    protected void registerTriggerStartHandler() {
        triggerManager.addEventHandler(TriggerEvent.START, (event, msg) -> {
            ExecutionContextStartMessage startMessage = (ExecutionContextStartMessage) msg;
            if (logger.isDebugEnabled())
                logger.debug(new AnnotatedExecutionContextStartMessage(mapper, startMessage));
        });
    }

    protected void registerTriggerFinishHandler() {
        triggerManager.addEventHandler(TriggerEvent.FINISH, (event, msg) -> {
            ExecutionContextFinishMessage finishMessage = (ExecutionContextFinishMessage) msg;
            if (logger.isDebugEnabled())
                logger.debug(new AnnotatedExecutionContextFinishMessage(mapper, finishMessage));
        });
    }
}
