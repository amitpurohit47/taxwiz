package com.taxwiz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Employee {
    @Id
    private String employeeId;
    private String firstName;
    private String lastName;
    private Integer userId;
    private String email;
    private String phone;
    private String password;

    @ManyToOne
    @JoinColumn(name = "firm_id")
    private Firm firm;

}
