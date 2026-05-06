package com.gym.crm.service.impl;

import com.gym.crm.annotation.PersistenceTx;
import com.gym.crm.dao.UserDao;
import com.gym.crm.dto.request.ActivationRequest;
import com.gym.crm.dto.request.ChangePasswordRequest;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.EntityValidationException;
import com.gym.crm.model.User;
import com.gym.crm.profile.PasswordEncoder;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.CoreValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private CoreValidator coreValidator;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setValidationService(CoreValidator coreValidator) {
        this.coreValidator = coreValidator;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    @PersistenceTx
    public void changePassword(ChangePasswordRequest request) {
        coreValidator.validate(request);
        log.info("Changing password for user: username={}", request.getUsername());

        User user = requireUserByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Password change failed - wrong old password: username={}", request.getUsername());
            throw new EntityValidationException("Old password does not match for user: " + request.getUsername());
        }

        update(user.toBuilder()
                .password(passwordEncoder.encode(request.getNewPassword()))
                .build());

        log.info("Password changed successfully for user: username={}", request.getUsername());
    }

    @Override
    @PersistenceTx
    public void activate(ActivationRequest request) {
        log.info("Activating user: username={}", request.getUsername());

        User user = requireUserByUsername(request.getUsername());

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new EntityValidationException("User is already active: " + request.getUsername());
        }

        update(user.toBuilder().isActive(true).build());
        log.info("User activated: username={}", request.getUsername());
    }

    @Override
    @PersistenceTx
    public void deactivate(ActivationRequest request) {
        log.info("Deactivating user: username={}", request.getUsername());

        User user = requireUserByUsername(request.getUsername());

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new EntityValidationException("User is already inactive: " + request.getUsername());
        }

        update(user.toBuilder().isActive(false).build());
        log.info("User deactivated: username={}", request.getUsername());
    }

    private User requireUserByUsername(String username) {
        return userDao.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    private void update(User user) {
        userDao.update(user);
    }
}