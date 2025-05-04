package com.taxwiz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String firstName;
    private String lastName;
    private Integer userId;
    private String email;
    private String phone;
    private String password;

    @ManyToOne
    @JoinColumn(name = "firm_id")
    private Firm firm;

    @PrePersist
    public void init() {
        if ( this.uid == null ) {
            this.uid = UUID.randomUUID().toString();
        }
    }
}
