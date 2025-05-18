package com.taxwiz.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignClientDto {
    private String clientUid;
    private String employeeUid;

    public AssignClientDto() {
    }

    public AssignClientDto(String clientUid, String employeeUid) {
        this.clientUid = clientUid;
        this.employeeUid = employeeUid;
    }
}
