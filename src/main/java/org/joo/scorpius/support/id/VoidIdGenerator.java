package org.joo.scorpius.support.id;

import java.util.Optional;

import org.joo.scorpius.support.builders.Factory;

public class VoidIdGenerator implements Factory<Optional<String>> {
	
	@Override
	public Optional<String> create() {
		return Optional.empty();
	}
}