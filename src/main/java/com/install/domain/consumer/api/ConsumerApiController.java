package com.install.domain.consumer.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
