package org.joo.scorpius.support.id;

import org.joo.scorpius.support.builders.Factory;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class TimeBasedIdGenerator implements Factory<String> {
	
	private TimeBasedGenerator generator;

	public TimeBasedIdGenerator() {
		generator = Generators.timeBasedGenerator();
	}

	@Override
	public String create() {
		return generator.generate().toString();
	}
}