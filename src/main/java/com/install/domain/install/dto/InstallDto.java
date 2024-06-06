package com.install.domain.install.dto;

import static lombok.AccessLevel.PRIVATE;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

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
  public static class InstallSearchCondition {

  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public static class InstallRequest {

    @NotBlank(message = "작업 유형을 입력해 주세요.")
    private String workTypeCd;

    private String comment;
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
  public static class InstallHistoryByModem {

    private String currentState;
    private Page<historyInfo> historys;

  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class InstallHistoryByConsumer {

    private String currentState;
    private Page<historyInfo> historys;
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class historyInfo {

    private LocalDateTime workTime;
    private String workType;
    private String consumerNo;
    private String modemNo;
    private String consumerName;
    private String meterNo;
    private String city;
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class InstallCount {

  }
}
