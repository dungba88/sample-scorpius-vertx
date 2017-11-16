package org.joo.scorpius.support.message;

import java.io.Serializable;

import org.joo.scorpius.support.BaseRequest;

public class ExecutionContextMessage implements Serializable {

	private static final long serialVersionUID = 4814185340706357939L;
	
	private final String id;
	
	private final String eventName;
	
	private final BaseRequest request;

	public ExecutionContextMessage(String id, String eventName, BaseRequest request) {
		this.id = id;
		this.eventName = eventName;
		this.request = request;
	}

	public String getId() {
		return id;
	}

	public String getEventName() {
		return eventName;
	}

	public BaseRequest getRequest() {
		return request;
	}
}
