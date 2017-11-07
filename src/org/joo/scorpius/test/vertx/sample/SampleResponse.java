package org.joo.scorpius.test.vertx.sample;

import org.joo.scorpius.test.vertx.support.BaseResponse;

public class SampleResponse extends BaseResponse {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
