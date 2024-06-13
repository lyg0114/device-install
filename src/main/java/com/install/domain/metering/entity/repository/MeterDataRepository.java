package com.install.domain.metering.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.install.domain.metering.entity.MeterData;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.entity.repository
 * @since : 11.06.24
 */
public interface MeterDataRepository extends JpaRepository<MeterData, Long> {

}
