package com.taxwiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponseDto {
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
}
