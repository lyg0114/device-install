package com.install.domain.metering.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.entity
 * @since : 11.06.24
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "meter_data")
@Entity
public class MeterData {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "meter_data_id")
  private Long id;

}
