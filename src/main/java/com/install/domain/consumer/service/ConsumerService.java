package com.install.domain.consumer.service;

import static com.install.global.exception.CustomErrorCode.CONSUMER_NOT_EXIST;
import static com.install.global.exception.CustomErrorCode.CONSUMER_NO_ALREADY_EXIST;
import static com.install.global.exception.CustomErrorCode.METER_NO_ALREADY_EXIST;

import com.install.domain.consumer.dto.ConsumerDto;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerRequest;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerResponse;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.service
 * @since : 04.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ConsumerService {

  private final ConsumerRepository consumerRepository;

  public Page<ConsumerResponse> searchConsumers(ConsumerSearchCondition condition, Pageable pageable) {
    return consumerRepository.searchConsumer(condition, pageable)
        .map(Consumer::toResponse);
  }

  public void addConsumer(ConsumerDto.ConsumerRequest requestDto) {
    validateDuplicateConsumerNo(requestDto.getConsumerNo());
    validateDuplicateMeterNo(requestDto.getMeterNo());
    consumerRepository.save(requestDto.toEntity());
  }

  public void updateConsumer(Long consumerId, ConsumerRequest requestDto) {
    consumerRepository.findById(consumerId)
        .orElseThrow(() -> new CustomException(CONSUMER_NOT_EXIST))
        .updateConsumer(requestDto);
  }

  public void deleteConsumer(Long consumerId) {
    Consumer consumer = consumerRepository.findById(consumerId)
        .orElseThrow(() -> new CustomException(CONSUMER_NOT_EXIST));
    consumerRepository.delete(consumer);
  }

  private void validateDuplicateConsumerNo(String consumerNo) {
    if (consumerRepository.existsByconsumerNo(consumerNo)) {
      throw new CustomException(CONSUMER_NO_ALREADY_EXIST);
    }
  }

  private void validateDuplicateMeterNo(String meterId) {
    if (consumerRepository.existsByMeterNo(meterId)) {
      throw new CustomException(METER_NO_ALREADY_EXIST);
    }
  }
}
