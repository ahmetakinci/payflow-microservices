package com.payflow.userservice.service;

import com.payflow.userservice.dto.RegisterRequest;
import com.payflow.userservice.entity.User;

import java.util.List;

public interface UserService {

    User register(RegisterRequest request);

    List<User> findAll();

    User findById(long id);

    void deleteById(long id);

}