package com.gt_enterprise.auth_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfulResponse<T> {
    private int status;
    private String message;
    private T data;

    public SuccessfulResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public SuccessfulResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
