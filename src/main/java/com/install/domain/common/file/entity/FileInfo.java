package com.install.domain.common.file.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.install.domain.common.BaseTimeEntity;
import com.install.domain.install.entity.InstallInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common.file.entity
 * @since : 07.06.24
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
public class FileInfo extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "file_info_id")
  private Long id;

  @Column(name = "file_uri")
  private String fileUri;

  @Column(name = "file_size")
  private Long fileSize;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "install_info_id")
  private InstallInfo installInfo;
}
