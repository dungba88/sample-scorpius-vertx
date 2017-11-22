package org.joo.scorpius.support.builders.contracts;

import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.builders.Factory;
import org.joo.scorpius.support.deferred.Deferred;
import org.joo.scorpius.support.exception.TriggerExecutionException;

public interface DeferredFactory extends Factory<Deferred<BaseResponse, TriggerExecutionException>> {

}
