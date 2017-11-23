package org.joo.scorpius.support.message;

import java.io.Serializable;

public class CustomMessage implements Serializable {

    private static final long serialVersionUID = 6888189228534475717L;

    private final String name;

    private final Serializable customObject;

    public CustomMessage(String name, Serializable customObject) {
        this.name = name;
        this.customObject = customObject;
    }

    public String getName() {
        return name;
    }

    public Serializable getCustomObject() {
        return customObject;
    }
}
