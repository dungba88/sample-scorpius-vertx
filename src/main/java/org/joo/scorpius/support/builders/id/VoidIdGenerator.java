package org.joo.scorpius.support.builders.id;

import java.util.Optional;

import org.joo.scorpius.support.builders.contracts.IdGenerator;

public class VoidIdGenerator implements IdGenerator {

    @Override
    public Optional<String> create() {
        return Optional.empty();
    }
}