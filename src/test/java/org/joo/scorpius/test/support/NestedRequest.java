package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseRequest;

public class NestedRequest extends BaseRequest {

	private String name;
	
	public NestedRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
