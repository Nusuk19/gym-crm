package com.gym.crm.dao;

import com.gym.crm.model.User;

import java.util.Optional;

public interface UserDao {

    User update(User user);

    Optional<User> findByUsername(String username);
}