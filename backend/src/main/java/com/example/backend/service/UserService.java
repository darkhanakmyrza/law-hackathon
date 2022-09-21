package com.example.backend.service;

import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.SignupRequest;
import com.example.backend.model.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {

  ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

  ResponseEntity<?> registerUser(SignupRequest signUpRequest);

  User getByPhone(String phone);
}
