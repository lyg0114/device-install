package com.install.domain.consumer.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.StringUtils.hasText;

import com.install.domain.common.BaseTimeEntity;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerRequest;
import com.install.domain.modem.entity.Modem;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity
 * @since : 04.06.24
 */
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "consumer",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"consumer_no"})
    })
@Entity
public class Consumer extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "consumer_id")
  private Long id;

  @Column(name = "consumer_no")
  private String consumerNo;

  @Column(name = "consumer_nm")
  private String consumerName;

  @Column(name = "meter_no")
  private String meterNo;

  @Column(name = "has_modem")
  private Boolean hasModem = false;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "installed_modem_id")
  private Modem installedModem;

  @Embedded
  private Address address;

  @Embedded
  private Location location;

  public void updateConsumer(ConsumerRequest consumerDto) {
    if (hasText(consumerDto.getConsumerNo())) { this.consumerNo = consumerDto.getConsumerNo(); }
    if (hasText(consumerDto.getConsumerName())) { this.consumerName = consumerDto.getConsumerName(); }
    if (hasText(consumerDto.getMeterNo())) { this.meterNo = consumerDto.getMeterNo(); }
    if (hasText(consumerDto.getCity())) { this.address.setCity(consumerDto.getCity()); }
    if (hasText(consumerDto.getStreet())) { this.address.setStreet(consumerDto.getStreet()); }
    if (hasText(consumerDto.getZipcode())) { this.address.setZipcode(consumerDto.getZipcode()); }
    if (hasText(consumerDto.getGeoX()) && hasText(consumerDto.getGeoY())) {
      this.location.setGeoX(consumerDto.getGeoX());
      this.location.setGeoY(consumerDto.getGeoY());
    }
  }

  public void installed() {
    this.hasModem = true;
  }

  public void demolish() {
    this.hasModem = false;
  }
}