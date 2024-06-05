package com.install.domain.install.service;

import static com.install.global.exception.CustomErrorCode.ALREADY_INSTALLED_MODEM;
import static com.install.global.exception.CustomErrorCode.CONSUMER_NOT_EXIST;
import static com.install.global.exception.CustomErrorCode.MODEM_NOT_EXIST;

import com.install.domain.code.entity.Code;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.exception.CustomException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.service
 * @since : 05.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class InstallService {

  private final InstallRepository installRepository;
  private final ModemRepository modemRepository;
  private final ConsumerRepository consumerRepository;

  /**
   * @param modemId
   * @param consumerId
   * @param requestDto
   *
   * 단말기 설치
   */
  public void installModem(Long modemId, Long consumerId, InstallDto.InstallRequest requestDto) {
    validateIsExistModem(modemId);
    validateIsExistConsumer(consumerId);
    validateIsInstalledModem(modemId);
    installRepository.save(
        InstallInfo.builder()
            .modem(Modem.builder().id(modemId).build())
            .consumer(Consumer.builder().id(consumerId).build())
            .comment(requestDto.getComment())
            .workTypeCd(Code.builder()
                .code(requestDto.getWorkTypeCd())
                .build())
            .workTime(LocalDateTime.now())
            .build());
  }

  /**
   * 시스템에 등록된 모뎀인가
   */
  private void validateIsExistModem(Long modemId) {
    if (!modemRepository.existsById(modemId)) {
      throw new CustomException(MODEM_NOT_EXIST);
    }
  }

  /**
   * 시스템에 등록된 고객정보 인가
   */
  private void validateIsExistConsumer(Long consumerId) {
    if (!consumerRepository.existsById(consumerId)) {
      throw new CustomException(CONSUMER_NOT_EXIST);
    }

  }

  /**
   * 이미 다른 고객에게 설치된 모뎀이 아닌가?
   */
  private void validateIsInstalledModem(Long modemId) {
    if (installRepository.isInstalledModem(modemId)) {
      throw new CustomException(ALREADY_INSTALLED_MODEM);
    }
  }
}
