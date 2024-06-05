package com.install.domain.consumer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository
 * @since : 04.06.24
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class Location {

  @Column(name = "geo_x")
  private String geoX;

  @Column(name = "geo_y")
  private String geoY;

}
