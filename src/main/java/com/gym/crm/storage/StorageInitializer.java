package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.RecordType;
import com.gym.crm.parser.CsvFileParser;
import com.gym.crm.reader.FileLineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer {

    private static final char DELIMITER = ',';

    @Value("${storage.init.file}")
    private Resource initFile;

    private FileLineReader fileLineReader;
    private CsvFileParser parser;

    @Autowired
    public void setFileLineReader(FileLineReader fileLineReader) {
        this.fileLineReader = fileLineReader;
    }

    @Autowired
    public void setParser(CsvFileParser parser) {
        this.parser = parser;
    }

    public Map<Long, Trainee> loadTrainees() {
        Map<Long, Trainee> result = new HashMap<>();

        readAllLines().stream()
                .filter(line -> extractType(line) == RecordType.TRAINEE)
                .map(parser::parseTrainee)
                .forEach(t -> result.put(t.getUserId(), t));

        return result;
    }

    public Map<Long, Trainer> loadTrainers() {
        Map<Long, Trainer> result = new HashMap<>();

        readAllLines().stream()
                .filter(line -> extractType(line) == RecordType.TRAINER)
                .map(parser::parseTrainer)
                .forEach(t -> result.put(t.getUserId(), t));

        return result;
    }

    public Map<Long, Training> loadTrainings() {
        Map<Long, Training> result = new HashMap<>();

        readAllLines().stream()
                .filter(line -> extractType(line) == RecordType.TRAINING)
                .map(parser::parseTraining)
                .forEach(t -> result.put(t.getTrainingId(), t));

        return result;
    }

    private List<String> readAllLines() {
        return fileLineReader.readLines(initFile);
    }

    private RecordType extractType(String line) {
        int idx = line.indexOf(DELIMITER);
        if (idx < 0) {
            throw new IllegalStateException("Invalid record format: " + line);
        }
        return RecordType.from(line.substring(0, idx));
    }
}