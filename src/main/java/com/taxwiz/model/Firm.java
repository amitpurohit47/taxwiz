package com.taxwiz.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Firm {
    @Id
    private String id;
    private String firmName;
    private String gstNo;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "firm", cascade = CascadeType.ALL)
    private List<Client> clients;

    @OneToMany(mappedBy = "firm", cascade = CascadeType.ALL)
    private List<Employee> employees;


}
