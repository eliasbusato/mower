package com.ebusato.mower.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Represents the possible movements of a mower.
 */
@AllArgsConstructor
public enum Movement {

    TURN_LEFT('L'),
    TURN_RIGHT('R'),
    MOVE_FORWARD('F');

    @Getter
    private char value;

    public static Movement getByValue(char value) {
        return Arrays.stream(Movement.values())
                .filter(v -> v.value == value).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("could not find enum for value ["+value+"]"));
    }
}
