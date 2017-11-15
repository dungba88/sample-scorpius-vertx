package org.joo.scorpius.test.support;

import org.joo.scorpius.support.BaseResponse;

public class SampleResponse extends BaseResponse {

	private static final long serialVersionUID = -7325467977709977459L;

	private String name;

	public SampleResponse() {
		
	}

	public SampleResponse(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
