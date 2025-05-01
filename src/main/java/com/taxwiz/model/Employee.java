package com.taxwiz.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    private String firstName;
    private String lastName;
    private Integer userId;
    private String email;
    private String phone;
    private String password;

    public Employee(String firstName, String lastName, Integer userId, String email, String phone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }
}
