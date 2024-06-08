package com.install.domain.install.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.install.domain.code.entity.Code;
import com.install.domain.common.BaseTimeEntity;
import com.install.domain.common.file.entity.FileInfo;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerResponse;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.member.entity.Member;
import com.install.domain.modem.entity.Modem;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.net.FileNameMap;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  // TODO : workTime 타임 포맷 셋팅하기
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

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id")
  private Member worker;

  @Builder.Default
  @OneToMany(mappedBy = "installInfo")
  private List<FileInfo> fileInfos = new ArrayList<>();

  @Override
  public String toString() {
    return "InstallInfo{" +
        "workTime=" + workTime +
        ", comment='" + comment + '\'' +
        ", consumerNo=" + consumer.getConsumerNo() +
        ", consumerName=" + consumer.getConsumerName() +
        ", modemNo =" + modem.getModemNo() +
        ", imei =" + modem.getImei() +
        ", workTypeCd.code =" + workTypeCd.getCode() +
        '}';
  }

  public ConsumerResponse toConsumerResponse() {
    return null;
  }
}
