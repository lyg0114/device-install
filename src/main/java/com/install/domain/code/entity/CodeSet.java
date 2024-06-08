package com.install.domain.code.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.code.entity
 * @since : 06.06.24
 */
@Getter
@RequiredArgsConstructor
public enum CodeSet {

  // 단말기 타입 코드
  MODEM_TYPE("cd01", "단말기종류", 1),

  // 단말기 상태 코드
  MODEM_STAUTS("cd02", "단말기상태", 1),

  // 단말기 설치상태 코드
  INSTALL_STATE("cd04", "설치상태", 1),
  HAS_MODEM("cd0401", "설치", 2),
  HAS_NOT_MODEM("cd0402", "미설치", 2),

  // 단말기 유지보수 코드
  MODEM_INSTALL_STATUS("cd03", "작업종류", 1),
  MODEM_INSTALL_STATUS_INSTALLED("cd0301", "신규설치", 2),
  MODEM_INSTALL_STATUS_MAINTANCE("cd0302", "유지보수", 2),
  MODEM_INSTALL_STATUS_CHANGE("cd0303", "교체", 2),
  MODEM_INSTALL_STATUS_DEMOLISH("cd0304", "철거", 2);

  private final String code;
  private final String name;
  private final Integer level;

  public static List<Code> getAllCodes() {
    List<Code> codes = new ArrayList<>();
    for (CodeSet codeSet : CodeSet.values()) {
      codes.add(Code.builder()
              .code(codeSet.getCode())
              .name(codeSet.getName())
              .level(codeSet.getLevel())
          .build());
    }
    return codes;
  }
}
