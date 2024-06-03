package com.install.domain.install.dto;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.dto
 * @since : 04.06.24
 */
public class InstallDto {

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public class InstallSearchCondition {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public static class InstallRequest {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class InstallResponse {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class ModemHistory {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class ConsumerHistory {
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class InstallCount {
  }
}
