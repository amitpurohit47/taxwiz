package com.taxwiz.dto;

import lombok.Getter;

@Getter
public class PasswordChange {
    private String newPassword;
    private String jwt;
}
