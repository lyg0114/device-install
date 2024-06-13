package com.install.domain.consumer.entity.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.consumer.entity.Consumer;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository
 * @since : 09.06.24
 */
public interface ConsumerRepositoryCustom {

	Page<Consumer> searchConsumer(ConsumerSearchCondition condition, Pageable pageable);
}
