package org.joo.scorpius.trigger.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joo.scorpius.trigger.TriggerEvent;
import org.joo.scorpius.trigger.TriggerEventDispatcher;
import org.joo.scorpius.trigger.TriggerEventHandler;

public class AbstractTriggerEventDispatcher implements TriggerEventDispatcher {

    private Map<TriggerEvent, List<TriggerEventHandler>> handlerMap = new HashMap<>();
    
    public void clearEventHandlers() {
    		handlerMap = new HashMap<>();
    }

    @Override
    public void addEventHandler(final TriggerEvent event, final TriggerEventHandler handler) {
        if (!handlerMap.containsKey(event)) {
            handlerMap.put(event, new ArrayList<>());
        }
        handlerMap.get(event).add(handler);
    }

    @Override
    public void notifyEvent(final TriggerEvent event, final Serializable msg) {
        if (!handlerMap.containsKey(event))
            return;
        for (TriggerEventHandler handler : handlerMap.get(event)) {
            handler.handleEvent(event, msg);
        }
    }

    @Override
    public boolean isEventEnabled(final TriggerEvent event) {
        return handlerMap.containsKey(event);
    }
}
