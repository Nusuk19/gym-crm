package com.gym.crm.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class FileLineReader {

    private static final Logger log = LoggerFactory.getLogger(FileLineReader.class);

    public List<String> readLines(Resource file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .toList();

        } catch (Exception e) {
            log.error("Failed to read storage init file: {}", file.getFilename());
            throw new IllegalStateException("Failed to read file", e);
        }
    }
}