package com.taxwiz.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uid;
    private String name;
    private String email;
    private String gstNo;
    private String phone;
    private String address;
    private boolean verified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id", nullable = false)
    private Firm firm;

    @PrePersist
    public void prePersist() {
        if ( this.uid == null ) {
            this.uid = java.util.UUID.randomUUID().toString();
        }
    }
}
