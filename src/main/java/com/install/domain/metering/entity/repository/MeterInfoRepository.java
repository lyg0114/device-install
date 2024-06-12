package com.install.domain.metering.entity.repository;

import com.install.domain.metering.entity.MeterInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.entity.repository
 * @since : 11.06.24
 */
public interface MeterInfoRepository extends JpaRepository<MeterInfo, Long> {
}
