package com.gym.crm.controller;

import com.gia.openapi.api.AuthApi;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AuthController implements AuthApi {

    private final GymFacade gymFacade;
    private final AuthMapper authMapper;

    @Override
    public ResponseEntity<Void> login(LoginRequest request) {
        gymFacade.login(authMapper.toCredentials(request));

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(LoginChangeRequest request) {
        gymFacade.changePassword(authMapper.toChangePassword(request));

        return ResponseEntity.ok().build();
    }
}
