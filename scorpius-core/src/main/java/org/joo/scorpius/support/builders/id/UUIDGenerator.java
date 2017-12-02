package org.joo.scorpius.support.builders.id;

import java.util.Optional;
import java.util.UUID;

import org.joo.scorpius.support.builders.contracts.IdGenerator;

public class UUIDGenerator implements IdGenerator {

    @Override
    public Optional<String> create() {
        return Optional.of(UUID.randomUUID().toString());
    }
}