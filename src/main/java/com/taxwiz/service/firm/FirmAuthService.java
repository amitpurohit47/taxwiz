package com.taxwiz.service.firm;

import com.taxwiz.dto.FirmDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FirmAuthService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public void registerFirm(FirmDto firmRegisterRequest) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    }
}
