package com.ebusato.mower.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Represents were a mower is facing.
 */
@AllArgsConstructor
public enum Orientation {

    NORTH('N'),
    EAST('E'),
    SOUTH('S'),
    WEST('W');

    @Getter
    private char value;

    private static final Orientation[] values = values();

    public Orientation next() { return values[(ordinal() + 1) % values.length]; }
    public Orientation previous() { return values[(ordinal() - 1  + values.length) % values.length]; }

    public static Orientation getByValue(char value) {
        return Arrays.stream(Orientation.values())
                .filter(v -> v.value == value).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("could not found enum for value ["+value+"]"));
    }
}