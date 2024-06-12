package com.install.domain.modem.api;

import com.install.domain.modem.dto.ModemDto;
import com.install.domain.modem.service.ModemService;
import com.install.global.websocket.handler.ProgressWebSocketHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.api
 * @since : 03.06.24
 */
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/modems/v1")
@RestController
public class ModemApiController {

  private final ModemService modemService;
  private final ProgressWebSocketHandler progressWebSocketHandler;

  /**
   *  - 단말기 설치 현황 카운트
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
        .body(modemService.searchModems(condition,pageable));
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
   * - 단말기 일괄 엑셀 등록
   */
  @PostMapping("/excel")
  public ResponseEntity<Void> addModemsByExcel(@RequestParam("file") MultipartFile file) {
    new Thread(() -> {
      try {
        processFile(file);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();

    return ResponseEntity.ok().build();
  }

  // 엑셀 업로드 작업을 위한 샘플 코드
  private void processFile(MultipartFile file) throws Exception {
    // 엑셀 파일을 처리하는 로직
    // 예를 들어, 각 행을 읽어서 처리하는 동안 진행 상황을 업데이트합니다.
    int totalRows = getTotalRows(file); // 전체 행 수를 가져오는 가상의 메서드
    for (int i = 0; i < totalRows; i++) {
      // 각 행을 처리하는 로직

      // 진행 상황 업데이트 (예: 10% 완료)
      int progress = (i + 1) * 100 / totalRows;
      progressWebSocketHandler.sendProgressUpdate(Integer.toString(progress));

      // 처리 속도 조절을 위한 예제 (실제 코드에서는 제거)
      Thread.sleep(50);
    }
  }

  private int getTotalRows(MultipartFile file) {
    // 엑셀 파일의 전체 행 수를 계산하는 로직
    // 예제에서는 간단히 100으로 설정
    return 100;
  }
}
