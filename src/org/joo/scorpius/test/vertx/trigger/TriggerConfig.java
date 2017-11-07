package org.joo.scorpius.test.vertx.trigger;

import java.lang.reflect.ParameterizedType;

import org.joo.scorpius.test.vertx.support.BaseRequest;
import org.joo.scorpius.test.vertx.support.BaseResponse;

public class TriggerConfig {

	private Trigger<? extends BaseRequest, ? extends BaseResponse> trigger;
	
	private Class<?> requestClass;
	
	public TriggerConfig(Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
		this.trigger = trigger;
		this.requestClass = getRequestClassFor(trigger);
	}

	private Class<?> getRequestClassFor(Trigger<? extends BaseRequest, ? extends BaseResponse> trigger) {
		return ((Class<?>) ((ParameterizedType) trigger.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	public Trigger<? extends BaseRequest, ? extends BaseResponse> getTrigger() {
		return trigger;
	}

	public Class<?> getRequestClass() {
		return requestClass;
	}
}
