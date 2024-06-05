package com.install.domain.consumer.api;


import com.install.domain.consumer.dto.ConsumerDto;
import com.install.domain.consumer.service.ConsumerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.api
 * @since : 03.06.24
 *
 *  - 수용가 조회
 *  - 수용가 등록
 *  - 수용가 수정
 *  - 수용가 삭제
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/consumers/v1")
@RestController
public class ConsumerApiController {

  private final ConsumerService consumerService;

  /**
   * - 수용가 조회
   */
  @GetMapping
  public ResponseEntity<Page<ConsumerDto.ConsumerResponse>> searchConsumers(
      ConsumerDto.ConsumerSearchCondition condition, Pageable pageable) {

    // business logic

    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  /**
   * - 수용가 등록
   */
  @PostMapping
  public ResponseEntity<Void> addConsumer(
      @RequestBody @Valid ConsumerDto.ConsumerRequest requestDto) {

    consumerService.addConsumer(requestDto);
    return ResponseEntity.ok().build();
  }

  /**
   * - 수용가 수정
   */
  @PatchMapping("/{consumerId}")
  public ResponseEntity<Void> updateConsumer(
      @PathVariable Long consumerId,
      @RequestBody @Valid ConsumerDto.ConsumerRequest requestDto
  ) {

    consumerService.updateConsumer(consumerId, requestDto);
    return ResponseEntity.ok().build();
  }

  /**
   * - 수용가 삭제
   */
  @DeleteMapping("/{modemId}")
  public ResponseEntity<Void> deleteItem(@PathVariable Long modemId) {

    // business logic

    return ResponseEntity.ok().build();
  }

  /**
   * - 수용가 일괄 엑셀 등록
   */
  @PostMapping("/excel")
  public ResponseEntity<Void> addConsumersByExcel(@RequestParam("file") MultipartFile file) {

    // business logic

    return ResponseEntity.ok().build();
  }

}
