package com.ebusato.mower.model;

import org.junit.Test;

import java.awt.Point;

import static com.ebusato.mower.model.constants.Movement.MOVE_FORWARD;
import static com.ebusato.mower.model.constants.Movement.TURN_LEFT;
import static com.ebusato.mower.model.constants.Movement.TURN_RIGHT;
import static com.ebusato.mower.model.constants.Orientation.EAST;
import static com.ebusato.mower.model.constants.Orientation.NORTH;
import static com.ebusato.mower.model.constants.Orientation.SOUTH;
import static com.ebusato.mower.model.constants.Orientation.WEST;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class MowerTest {

    @Test
    public void shouldSuccessfullyCreateAMower() {
        //when
        Mower m = new Mower("1 1 S");
        //then
        assertThat(m.getCoordinate(), is(equalTo(new Point(1,1))));
        assertThat(m.getOrientation(), is(equalTo(SOUTH)));
    }

    @Test
    public void shouldThrowExceptionIfMowerDataSizeIsInvalid() {
        //when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Mower("1 1 W R"));
        //then
        assertThat(exception.getMessage(), is("invalid mower data size."));
    }

    @Test
    public void shouldThrowExceptionIfCoordinatesAreInvalid() {
        //when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Mower("-1 1 E"));
        //then
        assertThat(exception.getMessage(), is("invalid mower coordinates."));
    }

    @Test
    public void shouldFaceEastAfterTurnRight() {
        //given
        Mower mower = createANewDefaultMower();
        //when
        mower.turn(TURN_RIGHT);
        //then
        assertThat(EAST, is(mower.getOrientation()));
    }

    @Test
    public void shouldFaceWestAfterTurnLeft() {
        //given
        Mower mower = createANewDefaultMower();
        //when
        mower.turn(TURN_LEFT);
        //then
        assertThat(WEST, is(mower.getOrientation()));
    }

    @Test
    public void shouldNotTurnWhenMovementIsMoveForward() {
        //given
        Mower mower = createANewDefaultMower();
        //when
        mower.turn(MOVE_FORWARD);
        //then
        assertThat(NORTH, is(mower.getOrientation()));
    }

    @Test
    public void shouldUpdateCoordinateWhenMovingForward() {
        //given
        Mower mower = createANewDefaultMower();
        //when
        mower.moveForward();
        //then
        assertThat(mower.getCoordinate(), is(equalTo(new Point(0,1))));
    }

    @Test
    public void shouldUpdateCoordinateWhenMovingBackward() {
        //given
        Mower mower = createANewDefaultMower();
        //when
        mower.moveForward();
        mower.moveBackward();
        //then
        assertThat(mower.getCoordinate(), is(equalTo(new Point())));
    }

    private Mower createANewDefaultMower() {
        return new Mower("0 0 N");
    }
}