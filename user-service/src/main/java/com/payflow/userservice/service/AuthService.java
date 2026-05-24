package com.payflow.userservice.service;

import com.payflow.userservice.dto.LoginRequest;
import com.payflow.userservice.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
