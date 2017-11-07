package org.joo.scorpius.test.vertx.sample;

import org.joo.scorpius.test.vertx.support.BaseRequest;

public class SampleRequest extends BaseRequest {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
