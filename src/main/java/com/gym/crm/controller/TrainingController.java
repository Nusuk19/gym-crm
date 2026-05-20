package com.gym.crm.controller;

import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.facade.GymFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.base-path}/trainings")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training management endpoints")
public class TrainingController {

    private final GymFacade facade;

    @PostMapping
    public ResponseEntity<Void> addTraining(@Valid @RequestBody TrainingCreateRequest request) {
        facade.createTraining(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {
        return ResponseEntity.ok(facade.findAllTrainingTypes());
    }
}