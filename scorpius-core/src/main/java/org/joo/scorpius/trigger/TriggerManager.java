package org.joo.scorpius.trigger;

import org.joo.promise4j.DoneCallback;
import org.joo.promise4j.FailCallback;
import org.joo.promise4j.Promise;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.LifeCycle;
import org.joo.scorpius.support.exception.MalformedRequestException;
import org.joo.scorpius.support.exception.TriggerExecutionException;
import org.joo.scorpius.support.message.PeriodicTaskMessage;
import org.joo.scorpius.trigger.handle.TriggerHandlingStrategy;

import net.jodah.failsafe.SyncFailsafe;

public interface TriggerManager extends TriggerEventDispatcher, LifeCycle {

    public BaseRequest decodeRequestForEvent(String name, String data) throws MalformedRequestException;

    public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data);

    public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data,
            DoneCallback<BaseResponse> doneCallback, FailCallback<TriggerExecutionException> failCallback);

    public Promise<BaseResponse, TriggerExecutionException> fire(String name, BaseRequest data,
            SyncFailsafe<Object> failSafe);
    
    public TriggerRegistration registerTrigger(String name);
    
    public TriggerRegistration registerTrigger(String name, TriggerConfig triggerConfig);

    public TriggerRegistration registerPeriodicEvent(PeriodicTaskMessage msg);

    public TriggerRegistration registerPeriodicEvent(PeriodicTaskMessage msg, TriggerConfig triggerConfig);

    public ApplicationContext getApplicationContext();

    public TriggerHandlingStrategy getHandlingStrategy();

    public void setHandlingStrategy(TriggerHandlingStrategy handlingStrategy);
}
