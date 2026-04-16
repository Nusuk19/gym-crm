package com.gym.crm.reader;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class FileLineReader {

    public List<String> readLines(Resource file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .toList();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to read file", e);
        }
    }
}