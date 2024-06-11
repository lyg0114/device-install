package com.install.domain.modem.dto;

import static lombok.AccessLevel.PRIVATE;

import com.install.domain.code.entity.Code;
import com.install.domain.common.dto.CodeDto;
import com.install.domain.modem.entity.Modem;
import jakarta.validation.constraints.NotBlank;
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
  public static class ModemSearchCondition {
    private String modemNo;
    private String consumerNo;
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor(access = PRIVATE)
  public static class ModemRequest {

    @NotBlank(message = "단말기번호를 입력 해주세요.")
    private String modemNo;

    @NotBlank(message = "imei를 입력 해주세요.")
    private String imei;

    @NotBlank(message = "제조사를 입력 해주세요.")
    private String buildCompany;

    @NotBlank(message = "단말기 종류를 입력 해주세요.")
    private String modemTypeCd;

    @NotBlank(message = "단말기 상태를 입력 해주세요.")
    private String modemStatusCd;

    public Modem toEntity() {
      return Modem.builder()
          .modemNo(this.modemNo)
          .imei(this.imei)
          .buildCompany(buildCompany)
          .modemTypeCd(Code.builder().code(modemTypeCd).build())
          .modemStatusCd(Code.builder().code(modemStatusCd).build())
          .build();
    }
  }

  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class ModemResponse {
    private String modemNo;
    private String consumerNo;
    private String imei;
    private String buildCompany;
    private CodeDto modemTypeCd;
    private CodeDto modemStatusCd;
  }


  @ToString
  @Getter
  @Builder
  @AllArgsConstructor
  public static class ModemInstallCount {
    private Long totalCount;
    private Long installedCount;
    private Long uninstalledCount;
  }
}
