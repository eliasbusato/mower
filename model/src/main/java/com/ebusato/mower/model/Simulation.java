package com.ebusato.mower.model;

import com.ebusato.mower.model.constants.Movement;
import lombok.Data;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Represents the simulation data.
 */
@Data
public class Simulation {

    private static final AtomicInteger mowerIdGenerator = new AtomicInteger(0);

    private final Point surfaceUpperCorner = new Point();

    private final Map<Integer,Mower> mowerList;
    private final Map<Integer, List<Movement>> movementList;

    /**
     * Creates a new simulation for the given surface data
     * @param data raw data [X Y]
     */
    public Simulation(String data) {
        this.build(data.split(" "));
        mowerList = new HashMap<>();
        movementList = new HashMap<>();
    }

    /**
     * Validates and construct the object.
     * @param data raw data [X Y]
     * @throws RuntimeException if invalid data is provided.
     */
    private void build(String[] data) throws RuntimeException {
        int[] upperPoint = Arrays.stream(data).mapToInt(Integer::parseInt).toArray();
        if (upperPoint.length != 2 || upperPoint[0] <= 0 || upperPoint[1] <= 0) {
            throw new IllegalArgumentException("invalid data for surface size. please check");
        }
        this.surfaceUpperCorner.move(upperPoint[0], upperPoint[1]);
    }

    /**
     * Adds a new @{@link Mower} to the simulation and returns its ID
     * @param mower mower to be added
     * @return mower id
     */

    public int addMower(Mower mower) {
        if (mowerList.values().stream()
                .anyMatch(m -> m.getCoordinate().equals(mower.getCoordinate()))) {
            throw new IllegalArgumentException("there is already a mower on the given " +
                    "position ["+mower.getCoordinate()+"]");
        }

        int key = mowerIdGenerator.incrementAndGet();
        mowerList.put(key, mower);
        return key;
    }

    /**
     * Adds a list of  @{@link Movement} to the given mower.
     * @param mowerId mower id
     * @param data raw data [MMMMMM] of movements.
     */
    public void addMovements(int mowerId, String data) {
        if (!mowerList.containsKey(mowerId)) {
            throw new NoSuchElementException("there is no mower with id ["+mowerId+"]");
        }
        List<Movement> movementList = data.chars()
                .mapToObj(c -> Movement.getByValue((char) c)).collect(Collectors.toList());
        this.movementList.put(mowerId, movementList);
    }
}