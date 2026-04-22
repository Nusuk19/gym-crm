package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.RecordType;
import com.gym.crm.parser.CsvFileParser;
import com.gym.crm.reader.FileLineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);
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
                .forEach(trainee -> result.put(trainee.getUserId(), trainee));
        log.info("Loaded {} trainee(s) from storage init file", result.size());

        return result;
    }

    public Map<Long, Trainer> loadTrainers() {
        Map<Long, Trainer> result = new HashMap<>();

        readAllLines().stream()
                .filter(line -> extractType(line) == RecordType.TRAINER)
                .map(parser::parseTrainer)
                .forEach(trainer -> result.put(trainer.getUserId(), trainer));
        log.info("Loaded {} trainer(s) from storage init file", result.size());

        return result;
    }

    public Map<Long, Training> loadTrainings() {
        Map<Long, Training> result = new HashMap<>();

        readAllLines().stream()
                .filter(line -> extractType(line) == RecordType.TRAINING)
                .map(parser::parseTraining)
                .forEach(training -> result.put(training.getTrainingId(), training));
        log.info("Loaded {} training(s) from storage init file", result.size());

        return result;
    }

    private List<String> readAllLines() {
        return fileLineReader.readLines(initFile);
    }

    private RecordType extractType(String line) {
        int idx = line.indexOf(DELIMITER);
        if (idx < 0) {
            log.error("Invalid record format, cannot extract type from line: [{}]", line);
            throw new IllegalStateException("Invalid record format: " + line);
        }

        return RecordType.from(line.substring(0, idx));
    }
}