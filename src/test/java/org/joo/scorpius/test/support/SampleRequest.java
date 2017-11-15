package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseRequest;

public class SampleRequest extends BaseRequest {

	private String name;
	
	public SampleRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
