package com.install.domain.modem.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.dto
 * @since : 03.06.24
 */
public class ModemDto {

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public class ModemSearchCondition {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public static class ModemRequest {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class ModemResponse {
  }
}
