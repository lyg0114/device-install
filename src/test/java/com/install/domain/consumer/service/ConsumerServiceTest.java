package com.install.domain.consumer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.install.domain.consumer.dto.ConsumerDto;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerRequest;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.service
 * @since : 04.06.24
 */
@Transactional
@SpringBootTest
class ConsumerServiceTest {

  @Autowired private ConsumerService consumerService;
  @Autowired private ConsumerRepository consumerRepository;

  private ConsumerRequest createConsumerRequest(Long id) {
     ConsumerDto.ConsumerRequest request = ConsumerRequest.builder()
        .consumerNo("consumerNo-" + id)
        .consumerName("consumerName" + id)
        .meterNo("meterNo-" + id)
        .city("city-" + id)
        .build();

     return request;
  }

  @Test
  void 고객정보_단건_등록에_성공한다() {
    //given
    ConsumerRequest consumerRequest = createConsumerRequest(1L);

    //when
    consumerService.addConsumer(consumerRequest);

    //then
    Consumer findConsumer = consumerRepository.findByConsumerNo("consumerNo-1").orElseThrow();

    assertThat(findConsumer.getConsumerNo()).isEqualTo(consumerRequest.getConsumerNo());
    assertThat(findConsumer.getConsumerName()).isEqualTo(consumerRequest.getConsumerName());
    assertThat(findConsumer.getMeterNo()).isEqualTo(consumerRequest.getMeterNo());
  }
}