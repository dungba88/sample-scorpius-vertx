package org.joo.scorpius.support.builders.contracts;

import org.joo.promise4j.Deferred;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.builders.Factory;
import org.joo.scorpius.support.exception.TriggerExecutionException;

public interface DeferredFactory extends Factory<Deferred<BaseResponse, TriggerExecutionException>> {

}
