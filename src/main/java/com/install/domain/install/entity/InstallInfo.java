package com.install.domain.install.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.install.domain.code.entity.Code;
import com.install.domain.common.BaseTimeEntity;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.member.entity.Member;
import com.install.domain.modem.entity.Modem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity
 * @since : 05.06.24
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
public class InstallInfo extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "install_info_id")
  private Long id;

  @Column(name = "work_time")
  private LocalDateTime workTime;

  @Column(name = "comment")
  private String comment;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "consumer_id")
  private Consumer consumer;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "modem_id")
  private Modem modem;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "work_type_cd")
  private Code workTypeCd;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "member_id")
  private Member worker;

}
