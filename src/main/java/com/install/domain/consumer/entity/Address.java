package com.install.domain.consumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity
 * @since : 04.06.24
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
public class Address {

  @Column(name = "address")
  private String address;

}
