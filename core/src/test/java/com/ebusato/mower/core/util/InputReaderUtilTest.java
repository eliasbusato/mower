package com.ebusato.mower.core.util;

import com.ebusato.mower.core.constants.CoreConstants;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class InputReaderUtilTest {

    @Test
    @SneakyThrows
    public void shouldLoadDefaultDataIfNoInputFileIsProvided() {
        //given
        Path inputFile = new ClassPathResource(CoreConstants.SAMPLE_DATA_FILENAME).getFile().toPath();
        //when
        InputReaderUtil readerUtil = new InputReaderUtil(null);
        //then
        assertThat(readerUtil.getPath(), is(inputFile));
    }

    @Test
    public void shouldThrowExceptionIfProvidedFileDoesNotExist() {
        //given
        String inputFile = Paths.get("anInvalidFile.err").toFile().getAbsolutePath();
        //when
        Throwable exception = assertThrows(NoSuchFileException.class, () -> new InputReaderUtil(inputFile));
        //then
        assertThat(exception.getMessage(), is("input file ["+inputFile+ "] does not exist! please check and try again"));
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionIfProvidedFileIsEmpty() {
        //given
        String inputFile = new ClassPathResource("empty-sample-data.txt").getFile().getAbsolutePath();
        //when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new InputReaderUtil(inputFile));
        //then
        assertThat(exception.getMessage(), is("no data provided. file is empty!"));
    }

    @Test
    @SneakyThrows
    public void shouldThrowExceptionIfProvidedHasAnInvalidSize() {
        //given
        String inputFile = new ClassPathResource("invalid-size-sample-data.txt").getFile().getAbsolutePath();
        //when
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> new InputReaderUtil(inputFile));
        //then
        assertThat(exception.getMessage(), is("input data is invalid. please check!"));
    }

    @Test
    public void shouldBeAbleToReadDefaultData() {
        //when
        InputReaderUtil readerUtil = new InputReaderUtil(null);
        //then
        assertThat(readerUtil.getSurfaceData(), is("5 5"));
        assertTrue(readerUtil.hasMoreInstructionData());
        assertThat(readerUtil.readNextMower(), is("0 0 N"));
        assertThat(readerUtil.redNextMovementList(), is("RFFF"));
        assertThat(readerUtil.readNextMower(), is("5 5 S"));
        assertThat(readerUtil.redNextMovementList(), is("FFFR"));
        assertFalse(readerUtil.hasMoreInstructionData());
    }

    @Test
    public void shouldReadMowerAndMovementDataBeforeQueryingForMoreData() {
        //given
        InputReaderUtil readerUtil = new InputReaderUtil(null);
        //when
        readerUtil.hasMoreInstructionData();
        readerUtil.readNextMower();
        Throwable exception = assertThrows(IllegalStateException.class,
                () -> readerUtil.hasMoreInstructionData());
        //then
        assertThat(exception.getMessage(), is("movement data " +
                "was not read before querying for more data!"));
    }

    @Test
    public void shouldNotBeAbleToReadMowerTwice() {
        //given
        InputReaderUtil readerUtil = new InputReaderUtil(null);
        //when
        readerUtil.readNextMower();
        Throwable exception = assertThrows(IllegalStateException.class,
                () -> readerUtil.readNextMower());
        //then
        assertThat(exception.getMessage(), is("movement data " +
                "must be read before querying for mower data!"));
    }

    @Test
    public void shouldNotBeAbleToReadMovementTwice() {
        //given
        InputReaderUtil readerUtil = new InputReaderUtil(null);
        //when
        readerUtil.readNextMower();
        readerUtil.redNextMovementList();
        Throwable exception = assertThrows(IllegalStateException.class,
                () -> readerUtil.redNextMovementList());
        //then
        assertThat(exception.getMessage(), is("mower data " +
                "must be read before querying for movement data!"));
    }
}
