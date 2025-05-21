package com.taxwiz.dto.employee;

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

    public EmployeeResponseDto(){}

    public EmployeeResponseDto(String uid, String firstName, String lastName, String email, String phone, String address) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
