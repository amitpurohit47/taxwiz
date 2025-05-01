package com.taxwiz.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private String clientName;
    private String gstNo;
    private String email;
    private String phone;
    private String address;

    public Client(String clientName, String gstNo, String email, String phone, String address) {
        this.clientName = clientName;
        this.gstNo = gstNo;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
