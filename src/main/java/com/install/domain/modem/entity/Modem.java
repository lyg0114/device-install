package com.install.domain.modem.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.install.domain.code.entity.Code;
import com.install.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.entity
 * @since : 05.06.24
 */
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "modem",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"modem_no"})
    }
)
@Entity
public class Modem extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "modem_id")
  private Long id;

  @Column(name = "modem_no")
  private String modemNo;

  @Column(name = "imei")
  private String imei;

  @Column(name = "build_company")
  private String buildCompany;

  @ManyToOne
  @JoinColumn(name = "modem_type_cd")
  private Code modemTypeCd;

  @ManyToOne
  @JoinColumn(name = "modem_status_cd")
  private Code modemStatusCd;

}
