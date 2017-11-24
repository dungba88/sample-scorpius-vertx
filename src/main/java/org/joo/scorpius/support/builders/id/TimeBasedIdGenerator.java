package org.joo.scorpius.support.builders.id;

import java.util.Optional;

import org.joo.scorpius.support.builders.contracts.IdGenerator;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class TimeBasedIdGenerator implements IdGenerator {

    private TimeBasedGenerator generator;

    public TimeBasedIdGenerator() {
        generator = Generators.timeBasedGenerator();
    }

    @Override
    public Optional<String> create() {
        return Optional.of(generator.generate().toString());
    }
}