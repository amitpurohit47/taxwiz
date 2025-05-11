package com.taxwiz.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    public EmployeeDto() {
    }

    public EmployeeDto(String firstName, String lastName, String email, String phone, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
