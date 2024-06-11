package com.install.domain.modem.service;

import static com.install.global.exception.CustomErrorCode.IMEI_ALREADY_EXIST;
import static com.install.global.exception.CustomErrorCode.MODEM_NOT_EXIST;
import static com.install.global.exception.CustomErrorCode.MODEM_NO_ALREADY_EXIST;

import com.install.domain.modem.dto.ModemDto.ModemRequest;
import com.install.domain.modem.dto.ModemDto.ModemResponse;
import com.install.domain.modem.dto.ModemDto.ModemSearchCondition;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 05.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ModemService {

  private final ModemRepository modemRepository;

  public Page<ModemResponse> searchModems(ModemSearchCondition condition, Pageable pageable) {
     return modemRepository.searchModems(condition,pageable)
         .map(Modem::toResponse);
  }

  public void addModem(ModemRequest requestDto) {
    validateDuplicateModemNo(requestDto.getModemNo());
    validateDuplicateImei(requestDto.getImei());
    modemRepository.save(requestDto.toEntity());
  }

  private void validateDuplicateModemNo(String modemNo) {
    if (modemRepository.existsByModemNo(modemNo)) {
      throw new CustomException(MODEM_NO_ALREADY_EXIST);
    }
  }

  private void validateDuplicateImei(String imei) {
    if (modemRepository.existsByImei(imei)) {
      throw new CustomException(IMEI_ALREADY_EXIST);
    }
  }

  public void updateModem(Long modemId, ModemRequest requestDto) {
    modemRepository.findById(modemId)
        .orElseThrow(() -> new CustomException(MODEM_NOT_EXIST))
        .updateModem(requestDto);
  }

  public void deleteModem(Long modemId) {
    Modem modem = modemRepository.findById(modemId)
        .orElseThrow(() -> new CustomException(MODEM_NOT_EXIST));
    modemRepository.delete(modem);
  }
}
