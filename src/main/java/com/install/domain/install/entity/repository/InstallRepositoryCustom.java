package com.install.domain.install.entity.repository;

import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.install.entity.InstallInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity.repository
 * @since : 05.06.24
 */
public interface InstallRepositoryCustom {

  Page<InstallInfo> searchInstallInfoPageByModem(Long modemId, Pageable pageable);

  Page<InstallInfo> searchInstallInfoPageByConsumer(Long consumerId, Pageable pageable);

  boolean isInstalledModem(Long modemId);

  Page<InstallInfo> searchConsumers(ConsumerSearchCondition condition, Pageable pageable);
}
