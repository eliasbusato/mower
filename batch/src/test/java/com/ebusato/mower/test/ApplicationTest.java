package com.ebusato.mower.test;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.ebusato.mower.core.constants.CoreConstants.RESULT_CONTEXT_OBJECT_NAME;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.io.FileMatchers.aReadableFile;
import static org.hamcrest.io.FileMatchers.anExistingFile;
import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BatchConfigurationTest.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ApplicationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void shouldSuccessfullyProcessSampleData1() throws Exception {
        //given
        JobExecution jobExecution = this.launchJobWithInputFile("sample-data-1.txt");
        //when
        String resultFileName = this.getResultFileName(jobExecution);
        File resultFile = Paths.get(resultFileName).toFile();
        List<String> fileLines = Files.readAllLines(resultFile.toPath(), StandardCharsets.UTF_8);
        //then
        assertThat(jobExecution.getExitStatus(), is(COMPLETED));
        assertThat(resultFile, is(anExistingFile()));
        assertThat(resultFile, is(aReadableFile()));
        assertThat(fileLines.get(0), is(equalTo("6 3 W")));
        assertThat(fileLines.get(1), is(equalTo("1 5 E")));
        assertThat(fileLines.get(2), is(equalTo("1 1 S")));
        assertThat(fileLines.get(3), is(equalTo("5 5 S")));
    }

    @Test
    public void shouldSuccessfullyProcessSampleData2() throws Exception {
        //given
        JobExecution jobExecution = this.launchJobWithInputFile("sample-data-2.txt");
        //when
        String resultFileName = this.getResultFileName(jobExecution);
        File resultFile = Paths.get(resultFileName).toFile();
        List<String> fileLines = Files.readAllLines(resultFile.toPath(), StandardCharsets.UTF_8);
        //then
        assertThat(jobExecution.getExitStatus(), is(COMPLETED));
        assertThat(resultFile, is(anExistingFile()));
        assertThat(resultFile, is(aReadableFile()));
        assertThat(fileLines.get(0), is(equalTo("1 3 E")));
        assertThat(fileLines.get(1), is(equalTo("3 1 N")));
    }

    @Test
    public void shouldSuccessfullyProcessSampleData3() throws Exception {
        //given
        JobExecution jobExecution = this.launchJobWithInputFile("sample-data-3.txt");
        //when
        String resultFileName = this.getResultFileName(jobExecution);
        File resultFile = Paths.get(resultFileName).toFile();
        List<String> fileLines = Files.readAllLines(resultFile.toPath(), StandardCharsets.UTF_8);
        //then
        assertThat(jobExecution.getExitStatus(), is(COMPLETED));
        assertThat(resultFile, is(anExistingFile()));
        assertThat(resultFile, is(aReadableFile()));
        assertThat(fileLines.get(0), is(equalTo("3 0 E")));
        assertThat(fileLines.get(1), is(equalTo("1 1 N")));
        assertThat(fileLines.get(2), is(equalTo("0 0 S")));
    }

    @SneakyThrows
    private JobExecution launchJobWithInputFile(String inputFileName) {
        String inputFile = new ClassPathResource(inputFileName).getFile().getAbsolutePath();
        JobParameters jobParameters = new JobParametersBuilder().addString("inputFile", inputFile).toJobParameters();
        return jobLauncherTestUtils.launchJob(jobParameters);
    }

    private String getResultFileName(JobExecution jobExecution) {
        return Objects.requireNonNull(jobExecution.getExecutionContext()
                .get(RESULT_CONTEXT_OBJECT_NAME)).toString();
    }
}