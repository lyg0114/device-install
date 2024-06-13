package com.install.domain.metering.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.install.domain.metering.dto.MeteringDto;
import com.install.domain.metering.service.MeteringService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.api
 * @since : 11.06.24
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/metering/v1")
@RestController
public class MeteringApiController {

	private final MeteringService meteringService;

	/**
	 * - 특정시점 검침정보 리스트 조회
	 */
	@GetMapping
	public ResponseEntity<Page<MeteringDto.MeteringResponse>> searchMeterInfo(
		MeteringDto.MeteringSearchCondition condition, Pageable pageable
	) {

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(meteringService.searchMeterInfo(condition, pageable));
	}
}
