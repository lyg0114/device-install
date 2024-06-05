package com.install.domain.consumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity
 * @since : 04.06.24
 */
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class Address {

  @Column(name = "city")
  private String city;

  @Column(name = "street")
  private String street;

  @Column(name = "zipcode")
  private String zipcode;

}
