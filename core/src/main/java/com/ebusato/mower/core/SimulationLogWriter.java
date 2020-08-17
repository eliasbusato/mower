package com.ebusato.mower.core;

import com.ebusato.mower.core.constants.CoreConstants;
import com.ebusato.mower.model.Mower;
import com.ebusato.mower.model.Simulation;
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

/**
 * Logs the @{@link Simulation} result.
 */
@Component
@Slf4j
public class SimulationLogWriter implements Tasklet, StepExecutionListener {

    private Simulation simulation;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        simulation = Simulation.class.cast(executionContext.get(CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME));
        log.info("simulation log writer initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        simulation.getMowerList().forEach((mowerId, mower) -> this.logResult(mower));
        return RepeatStatus.FINISHED;
    }

    private void logResult(Mower mower) {
        log.info("mower [{}] is positioned.", mower);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("simulation log writer completed.");
        return ExitStatus.COMPLETED;
    }
}
