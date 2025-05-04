package com.taxwiz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
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

    @PrePersist
    public void init() {
        if ( this.uid == null ) {
            this.uid = UUID.randomUUID().toString();
        }
    }

}
