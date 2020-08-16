package com.ebusato.mower.core;

import com.ebusato.mower.core.config.SinulationReaderConfig;
import com.ebusato.mower.model.Mower;
import com.ebusato.mower.model.Simulation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.Point;
import java.util.Arrays;

import static com.ebusato.mower.core.constants.CoreConstants.SIMULATION_CONTEXT_OBJECT_NAME;
import static com.ebusato.mower.model.constants.Movement.MOVE_FORWARD;
import static com.ebusato.mower.model.constants.Movement.TURN_RIGHT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SinulationReaderConfig.class)
@DirtiesContext(classMode = AFTER_CLASS)
public class SimulationReaderTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void shouldSuccessfullyLoadASimulateObjectInContext() throws Exception {
        //given
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("simulationReader");
        //when
        Simulation simulation = Simulation.class.cast(jobExecution.getExecutionContext().get(SIMULATION_CONTEXT_OBJECT_NAME));
        ExitStatus exitStatus = jobExecution.getExitStatus();
        //then
        assertThat(exitStatus, is(COMPLETED));
        assertThat(simulation.getSurfaceUpperCorner(), is(equalTo(new Point(5,5))));
        assertThat(simulation.getMowerList(), is(aMapWithSize(2)));
        assertThat(simulation.getMowerList(), hasValue(new Mower("0 0 N")));
        assertThat(simulation.getMowerList(), hasValue(new Mower("5 5 S")));
        assertThat(simulation.getMovementList(), hasValue(Arrays.asList(TURN_RIGHT, MOVE_FORWARD, MOVE_FORWARD, MOVE_FORWARD)));
        assertThat(simulation.getMovementList(), hasValue(Arrays.asList(MOVE_FORWARD, MOVE_FORWARD, MOVE_FORWARD, TURN_RIGHT)));
    }
}
