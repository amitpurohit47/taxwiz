package com.taxwiz.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Firm {
    private String firmName;
    private String gstNo;
    private String email;
    private String phone;
    private List<Client> clients;
    private List<Employee> employees;

    public Firm() {}

    public Firm(String firmName, String gstNo, String email, String phone, List<Client> clients, List<Employee> employees) {
        this.firmName = firmName;
        this.gstNo = gstNo;
        this.email = email;
        this.phone = phone;
        this.clients = clients;
        this.employees = employees;
    }

}
