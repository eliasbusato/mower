package com.ebusato.mower.core.config;

import com.ebusato.mower.core.SimulationProcessor;
import com.ebusato.mower.core._PackageMarker;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@Configuration
@EnableBatchProcessing
@ComponentScan(
        basePackageClasses = _PackageMarker.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = ASSIGNABLE_TYPE, value = SimulationProcessor.class)
        })
@Import(TestBatchConfig.class)
public class SinulationProcessorConfig {

    @Autowired
    public JobBuilderFactory jobs;
    @Autowired
    public StepBuilderFactory steps;

    @Bean
    protected Step processorStep(SimulationProcessor simulatorProcessor) {
        return steps.get("simulatorProcessor").tasklet(simulatorProcessor).build();
    }

    @Bean
    public Job job(Step processorStep) {
        return jobs
                .get("simulationJob")
                .start(processorStep)
                .build();
    }
}