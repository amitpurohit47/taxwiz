package com.taxwiz.model;

import lombok.Data;

@Data
public class FirmResponseEntity {
    private String firmName;
    private String passwordChangeLink;

    public FirmResponseEntity(String firmName, String passwordChangeLink) {

        this.firmName = firmName;
        this.passwordChangeLink = passwordChangeLink;
    }
}
