package com.install.domain.metering.service;

import com.install.domain.metering.dto.MeteringDto.MeteringResponse;
import com.install.domain.metering.dto.MeteringDto.MeteringSearchCondition;
import com.install.domain.metering.entity.MeterInfo;
import com.install.domain.metering.entity.repository.query.MeteringQueryRepository;
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

  private final MeteringQueryRepository meteringQueryRepository;

  public Page<MeteringResponse> searchMeterInfo(
      MeteringSearchCondition condition, Pageable pageable
  ) {
    return meteringQueryRepository.searchMeterInfo(condition, pageable)
        .map(MeterInfo::toMeteringResponse);
  }
}
