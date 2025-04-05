package com.gt_enterprise.auth_service.service;

import com.gt_enterprise.auth_service.dto.RegisterResponse;
import com.gt_enterprise.auth_service.entity.Role;
import com.gt_enterprise.auth_service.exception.IncorrectPasswordException;
import com.gt_enterprise.auth_service.exception.InvalidRoleException;
import com.gt_enterprise.auth_service.exception.UserAlreadyExistsException;
import com.gt_enterprise.auth_service.exception.UserNotFoundException;
import com.gt_enterprise.auth_service.repository.RoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gt_enterprise.auth_service.dto.AuthenticationRequest;
import com.gt_enterprise.auth_service.dto.AuthenticationResponse;
import com.gt_enterprise.auth_service.dto.RegisterRequest;
import com.gt_enterprise.auth_service.entity.User;
import com.gt_enterprise.auth_service.repository.UserRepository;
import com.gt_enterprise.auth_service.utils.JwtUtils;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public User register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        //Assign a Role to the new user if it is included in the request, if not assign to it the default role
        Role defaultRole = roleRepository.findByName(
                Optional.ofNullable(registerRequest.getRole()).orElse("ROLE_USER")//IMPORTANT REMOVE ROLE SELECTION IN PROD!
        ).orElseThrow(() -> new InvalidRoleException("Role not found"));

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Set.of(defaultRole));

        //Save new user in the database
        return userRepository.save(user);
    }

    public String login(AuthenticationRequest authenticationRequest) {

        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (bCryptPasswordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            String jwtToken = jwtUtils.generateToken(user); // Change here for anything you want to use as
                                                                          // unique identifier.
            return jwtToken;
        } else {
            throw new IncorrectPasswordException("The password is incorrect");
        }
    }
}
