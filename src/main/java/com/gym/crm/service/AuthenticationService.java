package com.gym.crm.service;

import com.gym.crm.dto.request.UserCredentials;

public interface AuthenticationService {

    void validateTraineeCredentials(UserCredentials credentials);

    void validateTrainerCredentials(UserCredentials credentials);

    void validateCredentials(UserCredentials credentials);
}