package com.taxwiz.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Client {
    @Id
    private String clientId;
    private String clientName;
    private String gstNo;
    private String email;
    private String phone;
    private String address;

    @ManyToOne
    @JoinColumn(name = "firm_id")
    private Firm firm;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
