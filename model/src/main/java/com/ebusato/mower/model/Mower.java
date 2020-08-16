package com.ebusato.mower.model;

import com.ebusato.mower.model.constants.Movement;
import com.ebusato.mower.model.constants.Orientation;
import lombok.Data;

import java.awt.Point;
import java.io.Serializable;

/**
 * Mower representation.
 */
@Data
public class Mower implements Serializable {

    private Point coordinate;
    private Orientation orientation;

    public Mower(String data) {
        this.build(data.split(" "));
    }

    /**
     * Validates and construct the object.
     * @param data raw data [X Y O]
     * @throws RuntimeException if invalid data is provided.
     */
    private void build(String[] data) throws RuntimeException {
        if (data.length != 3) {
            throw new IllegalArgumentException("invalid mower data size.");
        }
        orientation = Orientation.getByValue(data[2].charAt(0));
        int x = Integer.parseInt(data[0]);
        int y = Integer.parseInt(data[1]);
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("invalid mower coordinates.");
        }
        coordinate = new Point(x,y);
    }

    /**
     * Moves mower forward based on its current orientation.
     */
    public void moveForward() {
        int x = coordinate.x;
        int y = coordinate.y;
        switch (orientation) {
            case EAST:
                x++;
                break;
            case WEST:
                x--;
                break;
            case NORTH:
                y++;
                break;
            case SOUTH:
                y--;
                break;
        }
        coordinate.move(x,y);
    }

    /**
     * Moves mower backward based on its current orientation.
     */
    public void moveBackward() {
        int x = coordinate.x;
        int y = coordinate.y;
        switch (orientation) {
            case EAST:
                x--;
                break;
            case WEST:
                x++;
                break;
            case NORTH:
                y--;
                break;
            case SOUTH:
                y++;
                break;
        }
        coordinate.move(x,y);
    }

    /**
     * Turns mower based on given movement.
     * @param movement movement to execute.
     */
    public void turn(Movement movement) {
        Orientation newOrientation = this.orientation;
        switch (movement) {
            case TURN_RIGHT:
                newOrientation = this.orientation.next();
                break;
            case TURN_LEFT:
                newOrientation = this.orientation.previous();
                break;
        }
        this.setOrientation(newOrientation);
    }
}
