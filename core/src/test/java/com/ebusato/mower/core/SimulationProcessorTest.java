package com.ebusato.mower.core;

import com.ebusato.mower.core.config.SinulationProcessorConfig;
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

import java.awt.Point;

import static com.ebusato.mower.core.constants.CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SinulationProcessorConfig.class)
@DirtiesContext(classMode = AFTER_CLASS)
public class SimulationProcessorTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void shouldSuccessfullyProcessTheSimulation() {
        //given
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("simulatorProcessor", this.getExecutionContextForTest());
        //when
        Simulation simulation = Simulation.class.cast(jobExecution.getExecutionContext().get(SIMULATION_CONTEXT_OBJECT_NAME));
        ExitStatus exitStatus = jobExecution.getExitStatus();
        //then
        assertThat(exitStatus, is(COMPLETED));
        assertThat(simulation.getSurfaceUpperCorner(), is(equalTo(new Point(10,10))));
        assertThat(simulation.getMowerList(), is(aMapWithSize(3)));
        assertThat(simulation.getMowerList(), hasValue(new Mower("2 1 N")));
        assertThat(simulation.getMowerList(), hasValue(new Mower("4 2 S")));
        assertThat(simulation.getMowerList(), hasValue(new Mower("10 9 E")));
    }

    private ExecutionContext getExecutionContextForTest() {
        ExecutionContext executionContext = new ExecutionContext();
        Simulation simulation = new Simulation("10 10");
        int id1 = simulation.addMower(new Mower("1 1 E"));
        int id2 = simulation.addMower(new Mower("5 5 N"));
        int id3 = simulation.addMower(new Mower("9 9 S"));
        simulation.addMovements(id1, "FRFLLF");
        simulation.addMovements(id2, "LFLFFF");
        simulation.addMovements(id3, "RRRFFF");
        executionContext.put(SIMULATION_CONTEXT_OBJECT_NAME, simulation);
        return executionContext;
    }
}
