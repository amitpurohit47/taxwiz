package com.taxwiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {

    private String uid;
    private String name;
    private String email;
    private String gstNo;
    private String phone;
    private String address;

    public ClientDto() {}

    public ClientDto(String name, String email, String gstNo, String phone, String address) {
        this.name = name;
        this.email = email;
        this.gstNo = gstNo;
        this.phone = phone;
        this.address = address;
    }
    public ClientDto(String uid, String name, String email, String gstNo, String phone, String address) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gstNo = gstNo;
        this.phone = phone;
        this.address = address;
    }
}
