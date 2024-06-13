package com.install.domain.consumer.entity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.repository.query.ConsumerRepositoryCustom;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository
 * @since : 04.06.24
 */
public interface ConsumerRepository extends JpaRepository<Consumer, Long>,
	ConsumerRepositoryCustom {

	boolean existsByconsumerNo(String consumerNo);

	boolean existsByMeterNo(String meterNo);

	Optional<Consumer> findByConsumerNo(String consumerNo);
}
