package com.install.domain.install.dto;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import jakarta.validation.constraints.NotBlank;
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
	public static class InstallSearchCondition {

	}

	@ToString
	@Getter
	@Builder
	@AllArgsConstructor(access = PRIVATE)
	public static class InstallRequest {

		private String comment;

		@NotBlank(message = "설치일을 선택해 주세요.")
		private LocalDateTime workTime;
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
}
