package org.joo.scorpius.support.message;

import java.io.Serializable;

import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.TriggerExecutionException;

public class ExecutionContextExceptionMessage implements Serializable {

	private static final long serialVersionUID = 3083571156104110100L;

	private final String id;
	
	private final String eventName;
	
	private final BaseRequest request;
	
	private final TriggerExecutionException ex;

	public ExecutionContextExceptionMessage(String id, String eventName, BaseRequest request, TriggerExecutionException ex) {
		this.id = id;
		this.eventName = eventName;
		this.request = request;
		this.ex = ex;
	}

	public String getEventName() {
		return eventName;
	}

	public BaseRequest getRequest() {
		return request;
	}

	public TriggerExecutionException getCause() {
		return ex;
	}

	public String getId() {
		return id;
	}
}
