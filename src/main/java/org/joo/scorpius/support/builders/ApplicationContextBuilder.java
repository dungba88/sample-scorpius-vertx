package org.joo.scorpius.support.builders;

import org.joo.scorpius.ApplicationContext;

public class ApplicationContextBuilder implements Builder<ApplicationContext> {

	@Override
	public ApplicationContext build() {
		return new ApplicationContext();
	}
}
