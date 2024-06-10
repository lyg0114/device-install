package com.install.domain.consumer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.install.domain.consumer.dto.ConsumerDto;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerRequest;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import jakarta.persistence.EntityManager;
import java.util.NoSuchElementException;
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
@DisplayName("고객정보 CRUD 테스트")
@Transactional
@SpringBootTest
class ConsumerServiceTest {

  @Autowired ConsumerService consumerService;
  @Autowired ConsumerRepository consumerRepository;
  @Autowired EntityManager em;

  @Test
  void 고객정보_단건_등록에_성공한다() {
    //given
    ConsumerDto.ConsumerRequest consumerRequest = createConsumerRequest(1L);

    //when
    consumerService.addConsumer(consumerRequest);
    em.flush();
    em.clear();

    //then
    Consumer findConsumer = consumerRepository.findByConsumerNo("consumerNo-1").orElseThrow();

    assertThat(findConsumer.getConsumerNo()).isEqualTo(consumerRequest.getConsumerNo());
    assertThat(findConsumer.getConsumerName()).isEqualTo(consumerRequest.getConsumerName());
    assertThat(findConsumer.getMeterNo()).isEqualTo(consumerRequest.getMeterNo());
  }

  @Test
  void 고객정보_수정에_성공한다() {
    //given
    Consumer savedConsumer = consumerRepository.save(createConsumer(1L));
    ConsumerRequest consumerRequest = createConsumerRequest(2L);

    //when
    consumerService.updateConsumer(savedConsumer.getId(), consumerRequest);
    em.flush();
    em.clear();

    //then
    Consumer findConsumer = consumerRepository.findByConsumerNo("consumerNo-2")
        .orElseThrow();

    assertThat(findConsumer.getConsumerNo()).isEqualTo(consumerRequest.getConsumerNo());
    assertThat(findConsumer.getConsumerName()).isEqualTo(consumerRequest.getConsumerName());
    assertThat(findConsumer.getMeterNo()).isEqualTo(consumerRequest.getMeterNo());
  }

  @Test
  void 고객정보_삭제에_성공한다() {
    //given
    Consumer savedConsumer = consumerRepository.save(createConsumer(1L));
    Long targetConsumerId = savedConsumer.getId();

    //when
    consumerService.deleteConsumer(targetConsumerId);
    em.flush();
    em.clear();

    //then
    assertThrows(NoSuchElementException.class, () -> {
      consumerRepository.findById(targetConsumerId)
          .orElseThrow();
    });
  }

  private ConsumerRequest createConsumerRequest(Long id) {
    return ConsumerRequest.builder()
        .consumerNo("consumerNo-" + id)
        .consumerName("consumerName" + id)
        .meterNo("meterNo-" + id)
        .city("city-" + id)
        .build();
  }

  private Consumer createConsumer(Long id) {
    return Consumer.builder()
        .id(id)
        .consumerNo("consumerNo-" + id)
        .consumerName("consumerName" + id)
        .meterNo("meterNo-" + id)
        .address(Address.builder()
            .street("street-" + id)
            .city("city-" + id)
            .zipcode("zipcode-" + id)
            .build())
        .location(Location.builder()
            .geoX("getX-" + id)
            .geoY("getY-" + id)
            .build())
        .build();
  }

}