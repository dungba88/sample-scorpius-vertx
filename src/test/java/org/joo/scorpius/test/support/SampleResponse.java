package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;

public class SampleResponse extends BaseResponse {

	private String name;
	
	public SampleResponse(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
