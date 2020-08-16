package com.ebusato.mower.core.util;

import com.ebusato.mower.core.constants.CoreConstants;
import com.ebusato.mower.model.Mower;
import com.ebusato.mower.model.constants.Movement;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Utility class to validate and retrieve input data lines.
 */
@Slf4j
public class InputReaderUtil {

    @Getter
    private Path path;
    @Getter
    private final String surfaceData;

    private final LinkedList<String> data;

    @SneakyThrows({FileNotFoundException.class, IOException.class, IllegalArgumentException.class})
    public InputReaderUtil(String inputFile) {
        if (inputFile == null) {
            log.warn("argument 'inputPath' is missing. using default data for simulation!");
            this.path = new ClassPathResource(CoreConstants.SAMPLE_DATA_FILENAME).getFile().toPath();
        } else {
            this.path = Paths.get(inputFile);
            if (!path.toFile().exists()) {
                throw new NoSuchFileException("input file ["+path+"] does not exist! please check and try again");
            }
        }
        data = new LinkedList<>(Files.readAllLines(this.path, StandardCharsets.UTF_8));
        validateInput();
        surfaceData = data.pollFirst();
    }

    /**
     * Checks whenever input file has more @{@link Mower} and @{@link Movement} data to read.<br>
     * sets read state for data.
     * @return
     */
    public boolean hasMoreInstructionData() {
        if (data.size() % 2 != 0) {
            throw new IllegalStateException("movement data " +
                    "was not read before querying for more data!");
        }
        return data.size() > 0;
    }

    /**
     * Returns next @{@link Mower} data.
     * @return
     */
    public String readNextMower() {
        if (data.size() % 2 != 0) {
            throw new IllegalStateException("movement data " +
                    "must be read before querying for mower data!");
        }
        return data.pollFirst();
    }

    /**
     * Returns next @{@link Movement} list data.<br>
     * @return
     */
    public String redNextMovementList() {
        if (data.size() % 2 == 0) {
            throw new IllegalStateException("mower data " +
                    "must be read before querying for movement data!");
        }
        return data.pollFirst();
    }

    /**
     * Validates input data.
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     */
    private void validateInput() throws FileNotFoundException, IllegalArgumentException {
        if (CollectionUtils.isEmpty(data)) {
            throw new IllegalArgumentException("no data provided. file is empty!");
        }
        //input size should be odd and greater than 1
        if (data.size() == 1 || data.size() % 2 == 0) {
            throw new IllegalArgumentException("input data is invalid. please check!");
        }
    }
}