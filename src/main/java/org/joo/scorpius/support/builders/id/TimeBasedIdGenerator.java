package org.joo.scorpius.support.builders.id;

import java.util.Optional;

import org.joo.scorpius.support.builders.Factory;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class TimeBasedIdGenerator implements Factory<Optional<String>> {
	
	private TimeBasedGenerator generator;

	public TimeBasedIdGenerator() {
		generator = Generators.timeBasedGenerator();
	}

	@Override
	public Optional<String> create() {
		return Optional.of(generator.generate().toString());
	}
}