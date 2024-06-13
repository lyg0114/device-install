package com.install.domain.metering.dto;

import static lombok.AccessLevel.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.install.domain.code.dto.CodeDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.dto
 * @since : 11.06.24
 */
public class MeteringDto {

	@ToString
	@Getter
	@Builder
	@AllArgsConstructor(access = PRIVATE)
	public static class MeteringResponse {
		private String modemNo;
		private LocalDateTime meteringDate;
		private BigDecimal meteringUsage;
		private BigDecimal meteringData;
		private BigDecimal meteringTemp;
		private CodeDto meteringStateCd;
	}

	@ToString
	@Getter
	@Builder
	@AllArgsConstructor(access = PRIVATE)
	public static class MeteringSearchCondition {

		private LocalDateTime standardDate;
		private String modemNo;
		private String consumerNo;
	}
}
