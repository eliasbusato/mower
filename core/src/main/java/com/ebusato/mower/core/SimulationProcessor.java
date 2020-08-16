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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class SimulationProcessor implements Tasklet, StepExecutionListener {

    private Simulation simulation;
    private Map<Point, Integer> mowersPositions;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        simulation = Simulation.class.cast(
                executionContext.get(CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME));
        mowersPositions = new HashMap<>();
        simulation.getMowerList().forEach((mowerId, mower) -> mowersPositions.put(new Point(mower.getCoordinate()), mowerId));
        log.info("simulation initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        simulation.getMovementList().forEach((mowerId, movements) ->
                CompletableFuture.runAsync(() -> this.executeMovements(mowerId, movements)));
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("simulation processed.");
        return ExitStatus.COMPLETED;
    }

    private void executeMovements(Integer mowerId, List<Movement> movements) {
        movements.forEach(movement -> move(mowerId, movement));
    }

    private synchronized void move(Integer mowerId, Movement movement) {
        switch (movement) {
            case TURN_LEFT:
            case TURN_RIGHT:
                turn(mowerId, movement);
                break;
            default:
                moveForward(mowerId);
        }
        log.info("mower [{}] movement finished", mowerId);
    }

    private void moveForward(Integer mowerId) {
        Mower mower = simulation.getMowerList().get(mowerId);

        boolean shouldDiscard = false;
        Point currentlyCoordinate = new Point(mower.getCoordinate());

        log.info("moving mower [{}] forward", mower);
        mower.moveForward();
        if (isCoordinateOutsideSurface(mower.getCoordinate())) {
            log.warn("coordinate [{}] is outside the surface.", mower.getCoordinate());
            shouldDiscard = true;
        }
        if (isCoordinateUnavailable(mower.getCoordinate())) {
            log.warn("coordinate [{}] is unavailable", mower.getCoordinate());
            shouldDiscard = true;
        }
        if (shouldDiscard) {
            log.info("reverting mower movement!", mower);
            mower.moveBackward();
        } else {
            log.info("mower moved successfully!", mower);
            mowersPositions.remove(currentlyCoordinate);
            mowersPositions.put(new Point(mower.getCoordinate()), mowerId);
        }
    }

    private void turn(Integer mowerId, Movement movement) {
        Mower mower = simulation.getMowerList().get(mowerId);
        log.info("turning mower [{}] to [{}]", mower, movement);
        mower.turn(movement);
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