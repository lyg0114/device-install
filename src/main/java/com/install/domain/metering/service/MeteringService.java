package com.install.domain.metering.service;

import com.install.domain.metering.dto.MeteringDto.MeteringResponse;
import com.install.domain.metering.dto.MeteringDto.MeteringSearchCondition;
import com.install.domain.metering.entity.repository.MeterDataRepository;
import com.install.domain.metering.entity.repository.MeterInfoRepository;
import com.install.domain.metering.entity.repository.MeteringRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.service
 * @since : 11.06.24
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MeteringService {

  private final MeteringRepository meteringRepository;
  private final MeterDataRepository meterDataRepository;
  private final MeterInfoRepository meterInfoRepository;

  public Page<MeteringResponse> searchMeterInfo(
      MeteringSearchCondition condition, Pageable pageable
  ) {
    return null;
  }
}
