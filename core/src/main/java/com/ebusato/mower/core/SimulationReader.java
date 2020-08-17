package com.ebusato.mower.core;

import com.ebusato.mower.core.constants.CoreConstants;
import com.ebusato.mower.core.util.InputReaderUtil;
import com.ebusato.mower.model.Mower;
import com.ebusato.mower.model.Simulation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reads input data and puts the @{@link Simulation} to {@link ExecutionContext}
 */
@Component
@StepScope
@Slf4j
public class SimulationReader implements Tasklet, StepExecutionListener {

    private Simulation simulation;
    private InputReaderUtil inputReaderUtil;

    //used by tests
    @Value("#{jobParameters[inputFile] ?: null}")
    private String inputFile;

    public SimulationReader(@Value("${inputFile:#{null}}") String inputFile) {
        inputReaderUtil = new InputReaderUtil(inputFile);
    }

    /**
     * reloads input reader if an {@link #inputFile} was passed as a {@link JobParameter}
     * @param stepExecution stepExecution
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (inputFile != null) {
            log.info("job parameter found. reloading input reader...");
            inputReaderUtil = new InputReaderUtil(this.inputFile);
        }
        log.info("input file is [{}]", inputReaderUtil.getPath());
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        simulation = new Simulation(inputReaderUtil.getSurfaceData());

        while(inputReaderUtil.hasMoreInstructionData()) {
            int mowerId = simulation.addMower(new Mower(inputReaderUtil.readNextMower()));
            simulation.addMovements(mowerId, inputReaderUtil.redNextMovementList());
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put(CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME, this.simulation);
        log.info("simulation reader ended.");
        return ExitStatus.COMPLETED;
    }
}
