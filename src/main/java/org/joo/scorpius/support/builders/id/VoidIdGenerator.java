package org.joo.scorpius.support.builders.id;

import java.util.Optional;

public class VoidIdGenerator implements IdGenerator {
	
	@Override
	public Optional<String> create() {
		return Optional.empty();
	}
}