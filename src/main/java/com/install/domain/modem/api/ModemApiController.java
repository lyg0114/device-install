package com.install.domain.modem.api;

import static java.util.concurrent.Executors.*;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.modem.dto.ModemDto;
import com.install.domain.modem.service.ModemExcelService;
import com.install.domain.modem.service.ModemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.api
 * @since : 03.06.24
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/modems/v1")
@RestController
public class ModemApiController {

	private final ModemService modemService;
	private final ModemExcelService modemExcelService;
	private final ExecutorService executorService = newFixedThreadPool(10);

	/**
	 * - 단말기 설치 현황 카운트
	 */
	@GetMapping("/count")
	public ResponseEntity<ModemDto.ModemInstallCount> modeminstallCount() {

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(modemService.modeminstallCount());
	}

	/**
	 * - 단말기 리스트 조회
	 */
	@GetMapping
	public ResponseEntity<Page<ModemDto.ModemResponse>> searchModems(
		ModemDto.ModemSearchCondition condition, Pageable pageable) {

		return ResponseEntity
			.status(HttpStatus.OK)
			.body(modemService.searchModems(condition, pageable));
	}

	/**
	 * - 단말기 등록
	 */
	@PostMapping
	public ResponseEntity<Void> addModem(@RequestBody @Valid ModemDto.ModemRequest requestDto) {

		modemService.addModem(requestDto);
		return ResponseEntity.ok().build();
	}

	/**
	 * - 단말기 수정
	 */
	@PatchMapping("/{modemId}")
	public ResponseEntity<Void> updateModem(
		@PathVariable Long modemId,
		@RequestBody @Valid ModemDto.ModemRequest requestDto) {

		modemService.updateModem(modemId, requestDto);
		return ResponseEntity.ok().build();
	}

	/**
	 * - 단말기 삭제
	 */
	@DeleteMapping("/{modemId}")
	public ResponseEntity<Void> deleteModem(@PathVariable Long modemId) {

		modemService.deleteModem(modemId);
		return ResponseEntity.ok()
			.build();
	}

	/**
	 * - 단말기 일괄 엑셀 등록을 위한 세션 ID 생성
	 */
	@GetMapping("/excel/session")
	public ResponseEntity<String> createSession() {
		return ResponseEntity
			.ok(UUID.randomUUID().toString());
	}

	/**
	 * - 단말기 일괄 엑셀 등록
	 */
	// TODO : 동시에 두명이 엑셀을 업로드 했을때 예외 처리 필요
	@PostMapping("/excel")
	public ResponseEntity<Void> addModemsByExcel(@RequestParam("file") MultipartFile file, @RequestParam("sessionId") String sessionId) {
		executorService.submit(() -> modemExcelService.uploadModemExcel(file, sessionId));
		sleep(100);
		return ResponseEntity.ok()
			.build();
	}

	private static void sleep(long mill) {
		try {
			Thread.sleep(mill);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
