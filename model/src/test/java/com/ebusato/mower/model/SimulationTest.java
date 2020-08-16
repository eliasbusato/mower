package com.ebusato.mower.model;

import org.junit.Test;

import java.awt.Point;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.Assert.assertThrows;

public class SimulationTest {

    @Test
    public void shouldSuccessfullyCreateASimulation() {
        //when
        Simulation s = new Simulation("2 2");
        //then
        assertThat(s.getSurfaceUpperCorner(), is(equalTo(new Point(2,2))));
    }

    @Test
    public void shouldThrowExceptionIfSurfaceDataSizeIsInvalid() {
        //when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Simulation("5 5 5"));
        //then
        assertThat(exception.getMessage(), is("invalid data for surface size. please check"));
    }

    @Test
    public void shouldThrowExceptionIfSurfaceDataIsInvalid() {
        //when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new Simulation("-3 5"));
        //then
        assertThat(exception.getMessage(), is("invalid data for surface size. please check"));
    }

    @Test
    public void shouldSuccessfullyAddMowers() {
        //given
        Simulation s = createANewDefaultSimulation();
        //when
        int id1 = s.addMower(new Mower("1 1 E"));
        int id2 = s.addMower(new Mower("5 5 N"));
        int id3 = s.addMower(new Mower("9 9 S"));
        //then
        assertThat(s.getMowerList(), is(aMapWithSize(3)));
        assertThat(s.getMowerList(), hasKey(id1));
        assertThat(s.getMowerList(), hasKey(id2));
        assertThat(s.getMowerList(), hasKey(id3));
    }

    @Test
    public void shouldThrowAnExceptionWhenAddingMowersInSamePosition() {
        //given
        Simulation s = createANewDefaultSimulation();
        //when
        s.addMower(new Mower("5 5 N"));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> s.addMower(new Mower("5 5 S")));
        //then
        assertThat(exception.getMessage(), is("there is already a mower on the given " +
                "position ["+new Point(5,5)+"]"));
    }

    @Test
    public void shouldSuccessfullyAddMowerMovements() {
        //given
        Simulation s = createANewDefaultSimulation();
        //when
        int id1 = s.addMower(new Mower("1 1 E"));
        s.addMovements(id1, "FFLRLRFF");
        int id2 = s.addMower(new Mower("5 5 N"));
        s.addMovements(id2, "LRLRLRLRFFFF");
        //then
        assertThat(s.getMovementList(), is(aMapWithSize(2)));
        assertThat(s.getMovementList(), hasKey(id1));
        assertThat(s.getMovementList(), hasKey(id2));
    }

    @Test
    public void shouldSuccessfullyAddAMowerWithNoMovements() {
        //given
        Simulation s = createANewDefaultSimulation();
        //when
        int id1 = s.addMower(new Mower("1 1 E"));
        s.addMovements(id1, "FFLRLRFF");
        int id2 = s.addMower(new Mower("5 5 N"));
        s.addMovements(id2, "");
        //then
        assertThat(s.getMovementList(), is(aMapWithSize(2)));
        assertThat(s.getMovementList(), hasKey(id1));
        assertThat(s.getMovementList(), hasKey(id2));
    }

    @Test
    public void shouldThrowAnExceptionIfAddingMovementsForAnUnknownMower() {
        //given
        Simulation s = createANewDefaultSimulation();
        //when
        int id1 = s.addMower(new Mower("1 1 E"));
        s.addMovements(id1, "FFLRLRFF");
        int invalidId = 666;
        Throwable exception = assertThrows(NoSuchElementException.class, () -> s.addMovements(invalidId, "LRLRLRLRFFFF"));
        //then
        assertThat(exception.getMessage(), is("there is no mower with id ["+invalidId+"]"));
    }

    @Test
    public void shouldThrowAnExceptionIfAddingAnInvalidMovement() {
        //given
        Simulation s = createANewDefaultSimulation();
        //when
        int id1 = s.addMower(new Mower("1 1 E"));
        String invalidMovement = "X";
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> s.addMovements(id1, "FFLRLRFF"+invalidMovement));
        //then
        assertThat(exception.getMessage(), is("could not find enum for value ["+invalidMovement+"]"));
    }

    private Simulation createANewDefaultSimulation() {
        return new Simulation("10 10");
    }
}