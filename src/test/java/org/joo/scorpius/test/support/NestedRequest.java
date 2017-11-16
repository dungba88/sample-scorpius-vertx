package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseRequest;

public class NestedRequest extends BaseRequest {

	private static final long serialVersionUID = 4922398312068627241L;

	private String name;
	
	public NestedRequest(String traceId, String name) {
		super(traceId);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
