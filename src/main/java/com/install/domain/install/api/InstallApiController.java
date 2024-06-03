package com.install.domain.install.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.api
 * @since : 03.06.24
 *
 *  - 단말기 설치 내역 조회
 *  - 설치내역 조회
 *    - 단말기 기준으로 설치내역 조회
 *    - 수용가 기준으로 설치내역 조회
 *  - 단말기 설치
 *  - 단말기 철거
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/consumers/v1")
@RestController
public class InstallApiController {

}
