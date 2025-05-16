package com.taxwiz.dto.firm;

import lombok.Getter;

@Getter
public class FirmResponseDto {
    private String uid;
    private String name;
    private String gstNo;
    private String address;
    private String email;
    private String phone;

    public FirmResponseDto(String uid, String name, String gstNo, String address, String email, String phone) {
        this.uid = uid;
        this.name = name;
        this.gstNo = gstNo;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
}
