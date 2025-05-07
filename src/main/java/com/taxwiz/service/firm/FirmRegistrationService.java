package com.taxwiz.service.firm;

import com.taxwiz.dto.FirmDto;
import com.taxwiz.exception.AlreadyExistsException;
import com.taxwiz.model.Firm;
import com.taxwiz.repository.FirmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.taxwiz.utils.ErrorMessages.ALREADY_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirmRegistrationService {

    private final FirmRepository firmRepository;

    public Firm registerFirm(FirmDto firmDto) throws AlreadyExistsException {
        log.info("Registering firm: {}", firmDto.getName());

        Firm firm = firmRepository.findByGstNo(firmDto.getGstNo());
        if (firm != null) {
            throw new AlreadyExistsException(ALREADY_EXISTS.name());
        }
        firm = new Firm(firmDto.getName(), firmDto.getGstNo(), firmDto.getAddress(), firmDto.getEmail(), firmDto.getPhone());
        Firm savedFirm = firmRepository.save(firm);
        log.info("Firm registered successfully: {}", firm.getName());
        return savedFirm;
    }
}
