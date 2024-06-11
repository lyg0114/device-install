package com.install.domain.install.api;

import com.install.domain.install.dto.InstallDto;
import com.install.domain.install.dto.InstallDto.InstallHistoryByConsumer;
import com.install.domain.install.dto.InstallDto.InstallHistoryByModem;
import com.install.domain.install.service.InstallService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.api
 * @since : 03.06.24
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/install/v1")
@RestController
public class InstallApiController {

  private final InstallService installService;

  /**
   * - 단말기 기준으로 작업내역 조회
   */
  @GetMapping("/modem/{modemId}")
  public ResponseEntity<InstallHistoryByModem> searchHistoryByModem(
      @PathVariable Long modemId, Pageable pageable
  ) {

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(installService.searchHistoryByModem(modemId, pageable));
  }

  /**
   * - 고객정보 기준으로 작업내역 조회
   */
  @GetMapping("/consumer/{consumerId}")
  public ResponseEntity<InstallHistoryByConsumer> searchHistoryByConsumer(
      @PathVariable Long consumerId, Pageable pageable
  ) {

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(installService.searchHistoryByConsumer(consumerId, pageable));
  }

  /**
   * - 단말기 설치
   */
  @PostMapping("/{modemId}/{consumerId}")
  public ResponseEntity<Void> installModem(
      @PathVariable Long modemId,
      @PathVariable Long consumerId,
      @RequestBody @Valid InstallDto.InstallRequest requestDto,
      @RequestPart(value = "images", required = false) List<MultipartFile> images
  ) {
    installService.installModem(modemId, consumerId, requestDto, images);
    return ResponseEntity.ok().build();
  }

  /**
   * - 단말기 유지보수
   */
  @PatchMapping("/maintenance/{modemId}")
  public ResponseEntity<Void> maintenanceModem(
      @PathVariable Long modemId,
      @RequestBody @Valid InstallDto.InstallRequest requestDto,
      @RequestPart(value = "images", required = false) List<MultipartFile> images
  ) {

    installService.maintenanceModem(modemId, requestDto, images);
    return ResponseEntity.ok().build();
  }

  /**
   * - 단말기 철거
   */
  @PatchMapping("/demolish/{modemId}")
  public ResponseEntity<Void> demolishModem(
      @PathVariable Long modemId,
      @RequestBody @Valid InstallDto.InstallRequest requestDto,
      @RequestPart(value = "images", required = false) List<MultipartFile> images
  ) {

    installService.demolishModem(modemId, requestDto, images);
    return ResponseEntity.ok().build();
  }
}
