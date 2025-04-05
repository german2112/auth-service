package com.gt_enterprise.auth_service.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gt_enterprise.auth_service.entity.User;
import com.gt_enterprise.auth_service.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {// Implementation of
                                                                                             // UserDetails interface
        User user = userRepository.findByUsername(username) // Retrieves the user to get its username and password
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), // Creates
                                                                                                              // an
                                                                                                              // instance
                                                                                                              // of
                                                                                                              // UserDetails
                                                                                                              // with
                                                                                                              // user
                                                                                                              // username
                                                                                                              // and
                                                                                                              // password
                new ArrayList<>());
    }
}
