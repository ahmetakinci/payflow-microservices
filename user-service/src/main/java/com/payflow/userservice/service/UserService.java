package com.payflow.userservice.service;

import com.payflow.userservice.dto.RegisterRequest;
import com.payflow.userservice.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse register(RegisterRequest request);

    List<UserResponse > findAll();

    UserResponse findById(long id);

    void deleteById(long id);

    boolean existsById(Long id);
}