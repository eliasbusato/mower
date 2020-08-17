package com.ebusato.mower.batch;

import com.ebusato.mower.core.SimulationFileWriter;
import com.ebusato.mower.core.SimulationLogWriter;
import com.ebusato.mower.core.SimulationProcessor;
import com.ebusato.mower.core.SimulationReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@ComponentScan(basePackageClasses = {
        com.ebusato.mower.batch._PackageMarker.class,
        com.ebusato.mower.core._PackageMarker.class})
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobs;
    @Autowired
    public StepBuilderFactory steps;

    @Bean
    protected Step readerStep(SimulationReader simulationReader) {
        return steps.get("simulationReader").tasklet(simulationReader).build();
    }

    @Bean
    protected Step processorStep(SimulationProcessor simulatorProcessor) {
        return steps.get("simulatorProcessor").tasklet(simulatorProcessor).build();
    }

    @Bean
    protected Step writerStep(SimulationLogWriter simulationLogWriter, SimulationFileWriter simulationFileWriter) {
        return steps.get("simulationWriter")
                .tasklet(simulationLogWriter)
                .tasklet(simulationFileWriter)
                .build();
    }

    @Bean
    public Job job(Step readerStep, Step processorStep, Step writerStep) {
        return jobs
                .get("simulationJob")
                .start(readerStep)
                .next(processorStep)
                .next(writerStep)
                .build();
    }
}