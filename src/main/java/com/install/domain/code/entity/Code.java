package com.install.domain.code.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.install.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.code.entity
 * @since : 05.06.24
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Code extends BaseTimeEntity {

  @Id
  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "code_desc")
  private String desc;

  @Column(name = "code_level")
  private Integer level;

  // 셀프로 양방향 연관관계를 맺어서 계층구조를 구현
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_id")
  private Code parent;

  // 셀프로 양방향 연관관계를 맺어서 계층구조를 구현
  @OneToMany(mappedBy = "parent")
  private List<Code> child = new ArrayList<>();

  public void addChildCategory(Code child) {
    this.child.add(child);
    child.setParent(this);
  }
}
