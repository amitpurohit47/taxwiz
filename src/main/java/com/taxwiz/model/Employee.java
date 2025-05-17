package com.taxwiz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;

    /**
     * @ManyToOne
     *  private Firm firm;
     *
     *  Not adding this because there is no direct linkage between Employee and Firm
     *  Employee has User and User has Firm
     *  This creates a circular dependency and redundancy
     *  Instead, we'll use query
     */


    @OneToOne
    private User user;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Client> clients;

    @PrePersist
    private void prePersist() {
        if ( uid == null ) {
            uid = UUID.randomUUID().toString();
        }
    }
}
