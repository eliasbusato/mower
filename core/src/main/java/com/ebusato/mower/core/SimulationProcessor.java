package com.ebusato.mower.core;

import com.ebusato.mower.core.constants.CoreConstants;
import com.ebusato.mower.model.Mower;
import com.ebusato.mower.model.Simulation;
import com.ebusato.mower.model.constants.Movement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes the @{@link Simulation}
 */
@Component
@Slf4j
public class SimulationProcessor implements Tasklet, StepExecutionListener {

    private Simulation simulation;
    private Map<Point, Integer> mowersPositions;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        simulation = (Simulation) executionContext.get(CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME);
        if (simulation == null) {
            throw new IllegalStateException("simulation could not be retrieve from context");
        }
        mowersPositions = new HashMap<>();
        simulation.getMowerList().forEach((mowerId, mower) -> mowersPositions.put(new Point(mower.getCoordinate()), mowerId));
        log.info("simulation initialized.");
    }

    /**
     * Runs each @{@link Mower} @{@link Movement} list in parallel.
     * @param contribution contribution
     * @param chunkContext chunkContext
     * @return RepeatStatus.FINISHED
     * @throws InterruptedException if hangs
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws InterruptedException {
        List<Callable<Object>> tasks = new ArrayList<>();
        simulation.getMovementList().forEach((mowerId, movements) ->
                tasks.add(Executors.callable(this.executeMovements(mowerId, movements))));
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorService.invokeAll(tasks);
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("simulation processed.");
        return ExitStatus.COMPLETED;
    }

    private Runnable executeMovements(Integer mowerId, List<Movement> movements) {
        return () -> movements.forEach(movement -> move(mowerId, movement));
    }

    private void move(Integer mowerId, Movement movement) {
        switch (movement) {
            case TURN_LEFT:
            case TURN_RIGHT:
                turn(mowerId, movement);
                break;
            default:
                moveForward(mowerId);
        }
        log.debug("mower [{}] movement finished.", mowerId);
    }

    /**
     * Move @Mower in a synchronous way because of possible collisions and mower positions updates.
     * @param mowerId id of the mover to be moved
     */
    private synchronized void moveForward(Integer mowerId) {
        Mower mower = simulation.getMowerList().get(mowerId);

        boolean shouldDiscard = false;
        Point currentlyCoordinate = new Point(mower.getCoordinate());

        mower.moveForward();
        log.debug("mower [{}] moved forward. it is now at [{}].", mowerId, mower.getCoordinate());
        if (isCoordinateOutsideSurface(mower.getCoordinate())) {
            log.warn("mower [{}] outside the surface at coordinate [{}].", mowerId, mower.getCoordinate());
            shouldDiscard = true;
        }
        if (isCoordinateUnavailable(mower.getCoordinate())) {
            log.warn("mower [{}] is at an unavailable coordinate [{}].", mowerId, mower.getCoordinate());
            shouldDiscard = true;
        }
        if (shouldDiscard) {
            log.warn("mower [{}] movement will be reverted.", mowerId);
            mower.moveBackward();
        } else {
            log.debug("mower [{}] moved successfully.", mowerId);
            mowersPositions.remove(currentlyCoordinate);
            mowersPositions.put(new Point(mower.getCoordinate()), mowerId);
        }
    }

    /**
     * Tuns can safely run in parallel
     * @param mowerId id on the mowel to be turned
     * @param movement movement
     */
    private void turn(Integer mowerId, Movement movement) {
        Mower mower = simulation.getMowerList().get(mowerId);
        log.debug("mower [{}] will [{}]", mowerId, movement);
        mower.turn(movement);
        log.debug("mower [{}] is now facing [{}]", mowerId, mower.getOrientation());
    }

    private boolean isCoordinateUnavailable(Point coordinate) {
        return mowersPositions.get(coordinate) != null;
    }

    private boolean isCoordinateOutsideSurface(Point coordinate) {
        //for the sake of readability
        double mowerX = coordinate.getX();
        double mowerY = coordinate.getY();
        double surfaceX = simulation.getSurfaceUpperCorner().getX();
        double surfaceY = simulation.getSurfaceUpperCorner().getY();

        return (mowerX < 0 || mowerY < 0) || (mowerX > surfaceX || mowerY > surfaceY);
    }
}