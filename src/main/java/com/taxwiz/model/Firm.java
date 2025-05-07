package com.taxwiz.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
public class Firm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String name;

    @Column(unique = true)
    private String gstNo;
    private String address;
    private String email;
    private String phone;

    public Firm() {}

    public Firm(String name, String gstNo, String address, String email, String phone) {
        this.name = name;
        this.gstNo = gstNo;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    @PrePersist
    public void prePersist() {
        if ( this.uid == null ) {
            this.uid = UUID.randomUUID().toString();
        }
    }
}
