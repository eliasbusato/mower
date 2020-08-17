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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writes the @{@link Simulation} result in a file
 */
@Component
@Slf4j
public class SimulationFileWriter implements Tasklet, StepExecutionListener {

    private Simulation simulation;

    private String resultFilename;

    private static final String OUTPUT_DIR = System.getProperty("user.home");
    private static final String FILENAME_PREFIX = "simulation_result_";
    private static final String LINE_RESULT_FORMAT = "%d %d %s%n";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
        simulation = Simulation.class.cast(executionContext.get(CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME));
        log.info("simulation file writer initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws IOException {
        Path filePath = Paths.get(OUTPUT_DIR+File.separator+FILENAME_PREFIX+System.currentTimeMillis()+".txt");
        StringBuilder builder = new StringBuilder();
        simulation.getMowerList().forEach((mowerId, mower) -> this.buildResultLine(builder, mower));
        byte[] bytes = builder.toString().getBytes();

        log.info("writing simulation result file under [{}]", filePath);
        Files.write(filePath, bytes);
        this.resultFilename = filePath.toString();
        return RepeatStatus.FINISHED;
    }

    private void buildResultLine(StringBuilder builder, Mower mower) {
        builder.append(String.format(LINE_RESULT_FORMAT,
                (int)mower.getCoordinate().getX(),
                (int)mower.getCoordinate().getY(),
                            mower.getOrientation().getValue()));
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("simulation file writing results completed.");
        stepExecution
                .getJobExecution()
                .getExecutionContext()
                .put(CoreConstants.RESULT_CONTEXT_OBJECT_NAME, this.resultFilename);
        return ExitStatus.COMPLETED;
    }
}
