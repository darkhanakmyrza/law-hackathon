package com.example.backend.service;

import com.example.backend.config.security.jwt.JwtUtils;
import com.example.backend.model.dto.JwtResponse;
import com.example.backend.model.dto.LoginRequest;
import com.example.backend.model.dto.MessageResponse;
import com.example.backend.model.dto.SignupRequest;
import com.example.backend.model.entity.Role;
import com.example.backend.model.entity.User;
import com.example.backend.model.enumer.ERole;
import com.example.backend.repository.RoleRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.backend.constants.ServiceConstants.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private UserRepository userRepository;
  private RoleRepository roleRepository;
  private PasswordEncoder encoder;
  private AuthenticationManager authenticationManager;
  private JwtUtils jwtUtils;
  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  @Override
  public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getPhone(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication, getByPhone(loginRequest.getPhone()).getId());

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(
        jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        roles));
  }

  @Override
  public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getPhone())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NAME_ALREADY_TAKEN);
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EMAIL_ALREADY_USED);
    }

    // Create new user's account
    User user = new User();
    user.setPhone(signUpRequest.getPhone());
    user.setEmail(signUpRequest.getEmail());
    user.setPassword(encoder.encode(signUpRequest.getPassword()));

    Set<Role> roles = new HashSet<>();
    Role clientRole = roleRepository.findByName(ERole.ROLE_CLIENT
    ).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND));
    roles.add(clientRole);
    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse(USER_REGISTERED_SUCCESSFULLY));
  }

  @Override
  public User getByPhone(String phone) {
    User user = userRepository.findByUsername(phone)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_WITH_NAME + phone));
    return user;
  }
}
