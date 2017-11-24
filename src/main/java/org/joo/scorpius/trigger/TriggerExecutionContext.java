package org.joo.scorpius.trigger;

import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.Traceable;
import org.joo.scorpius.support.deferred.Promise;
import org.joo.scorpius.support.exception.TriggerExecutionException;

public interface TriggerExecutionContext extends Traceable {

    public void pending();

    public void execute();

    public void finish(BaseResponse response);

    public void fail(TriggerExecutionException ex);

    public Promise<BaseResponse, TriggerExecutionException> promise();

    public TriggerConfig getConfig();

    public BaseRequest getRequest();

    public TriggerExecutionStatus getStatus();

    public ApplicationContext getApplicationContext();

    public TriggerManager getTriggerManager();

    public String getId();

    public String getEventName();
}
