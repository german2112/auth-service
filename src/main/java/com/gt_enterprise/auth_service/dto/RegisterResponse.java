package com.gt_enterprise.auth_service.dto;

import com.gt_enterprise.auth_service.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RegisterResponse {

    private Long userId;
    private String userName;
    private String email;
    private List<String> roles;

    public RegisterResponse(Long userId, String username, String email, Set<Role> roles) {
        this.userId = userId;
        this.userName = username;
        this.email = email;
        this.roles = roles.stream().
                map(Role::getName).
                toList();
    }
}