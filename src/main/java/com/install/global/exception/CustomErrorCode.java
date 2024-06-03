package com.install.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author : iyeong-gyo
 * @package : com.apipractice.global.exception
 * @since : 18.05.24
 */
@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {
  // General
  INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, "잘못된 Http Method 요청입니다."),
  INVALID_VALUE(BAD_REQUEST, "잘못된 입력값입니다."),
  ACCESS_DENIED(UNAUTHORIZED, "접근권한이 없습니다."),
  UNKNON_INVALID_VALUE(BAD_REQUEST, "입력값을 확인해 주세요."),
  SERVER_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

  // JWT
  TOKEN_NOT_EXIST(BAD_REQUEST, "JWT Token이 존재하지 않습니다."),
  INVALID_TOKEN(BAD_REQUEST, "유효하지 않은 JWT Token 입니다."),
  REFRESH_TOKEN_EXPIRED(BAD_REQUEST, "만료된 Refresh Token 입니다."),
  ACCESS_TOKEN_EXPIRED(BAD_REQUEST, "만료된 Access Token 입니다."),

  // User
  EMAIL_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 메일주소 입니다."),
  NICKNAME_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 닉네임입니다."),
  LOGIN_FAILED(UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
  USER_NOT_FOUND(NOT_FOUND, "사용자를 찾을 수 없습니다."),
  USER_NOT_HAVE_ROLE(NOT_FOUND, "권한을 찾을 수 없습니다."),

  // Item
  ITEM_NOT_EXIST(BAD_REQUEST, "존재하지 않는 상품 입니다."),
  ITEMTYPE_CANNOT_CHANGE(BAD_REQUEST, "상품 종류는 변경할 수 없습니다."),
  ITEM_TYPE_NOT_EXIST(BAD_REQUEST, "존재하지 않는 상품 목록 입니다."),
  ITEM_SELLER_NOT_MATCH(BAD_REQUEST, "판매자 정보가 일치하지 않습니다."),
  ITEM_NAME_ALREADY_EXIST(BAD_REQUEST, "이미 등록된 상품명 입니다.");


  private final HttpStatus httpStatus;
  private final String errorMessage;
}
