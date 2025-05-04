package com.taxwiz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Firm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String firmName;

    @Column(unique = true)
    private String gstNo;
    private String email;
    private String phone;
    private String password;

    @OneToMany(mappedBy = "firm", cascade = CascadeType.ALL)
    private List<Client> clients;

    @OneToMany(mappedBy = "firm", cascade = CascadeType.ALL)
    private List<Employee> employees;

    public Firm() {
    }

    public Firm(String firmName, String gstNo, String email, String phone) {
        this.firmName = firmName;
        this.gstNo = gstNo;
        this.email = email;
        this.phone = phone;
    }

    @PrePersist
    public void init() {
        if ( this.uid == null ) {
            this.uid = UUID.randomUUID().toString();
        }
    }

}
