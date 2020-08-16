package com.ebusato.mower.core;

import com.ebusato.mower.core.config.SinulationWriterConfig;
import com.ebusato.mower.model.Mower;
import com.ebusato.mower.model.Simulation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.ebusato.mower.core.constants.CoreConstants.RESULT_CONTEXT_OBJECT_NAME;
import static com.ebusato.mower.core.constants.CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.aReadableFile;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SinulationWriterConfig.class)
@DirtiesContext(classMode = AFTER_CLASS)
public class SimulationWriterTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void shouldSuccessfullyWritesSimulationResult() throws IOException {
        //given
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("simulationWriter", this.getExecutionContextForTest());
        //when
        Simulation simulation = Simulation.class.cast(jobExecution.getExecutionContext().get(SIMULATION_CONTEXT_OBJECT_NAME));
        String resultFileName = jobExecution.getExecutionContext().get(RESULT_CONTEXT_OBJECT_NAME).toString();
        File resultFile = Paths.get(resultFileName).toFile();
        List<String> fileLines = Files.readAllLines(resultFile.toPath(), StandardCharsets.UTF_8);
        ExitStatus exitStatus = jobExecution.getExitStatus();
        //then
        assertThat(exitStatus, is(COMPLETED));
        assertThat(resultFile, is(anExistingFile()));
        assertThat(resultFile, is(aReadableFile()));
        assertThat(fileLines.get(0), is(equalTo("1 1 E")));
        assertThat(fileLines.get(1), is(equalTo("10 10 S")));
    }

    private ExecutionContext getExecutionContextForTest() {
        ExecutionContext executionContext = new ExecutionContext();
        Simulation simulation = new Simulation("10 10");
        int id1 = simulation.addMower(new Mower("1 1 E"));
        int id2 = simulation.addMower(new Mower("10 10 S"));
        simulation.addMovements(id1, "FFFRFFF");
        simulation.addMovements(id2, "FFFRFFF");
        executionContext.put(SIMULATION_CONTEXT_OBJECT_NAME, simulation);
        return executionContext;
    }
}
