package com.install.domain.consumer.entity.repository;

import com.install.domain.consumer.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository
 * @since : 04.06.24
 */
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {

  boolean existsByconsumerNo(String consumerNo);

  boolean existsByMeterNo(String meterNo);
}
