package com.install.domain.consumer.dto;

import static lombok.AccessLevel.PRIVATE;

import com.install.domain.consumer.entity.Consumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.dto
 * @since : 03.06.24
 */
public class ConsumerDto {


  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public class ConsumerSearchCondition {

  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public static class ConsumerRequest {

    private String consumerNo;
    private String consumerName;
    private String meterNo;
    private String city;
    private String street;
    private String zipcode;
    private String geoX;
    private String geoY;

    public Consumer toEntity() {
      return null;
    }
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class ConsumerResponse {

  }

}
