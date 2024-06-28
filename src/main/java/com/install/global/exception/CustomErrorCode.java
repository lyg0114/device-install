package com.install.global.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

	// Server Error (5XX)
	FAIL_ITIT_FILE_DIRECTORY(INTERNAL_SERVER_ERROR, "fail to initialize file directories"),

	// General
	INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, "잘못된 Http Method 요청입니다."),
	INVALID_VALUE(BAD_REQUEST, "잘못된 입력값입니다."),
	ACCESS_DENIED(UNAUTHORIZED, "접근권한이 없습니다."),
	UNKNON_INVALID_VALUE(BAD_REQUEST, "입력값을 확인해 주세요."),
	SERVER_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

	// File
	FILE_NOT_EXIST(BAD_REQUEST, "FILE이 존재하지 않습니다."),
	FILE_STORE_ERROR(BAD_REQUEST, "FILE 저장에 실패하였습니다."),

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

	// Consumer
	CONSUMER_NOT_EXIST(BAD_REQUEST, "고객정보를 찾을 수 없습니다."),
	CONSUMER_NO_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 고객번호 입니다."),
	METER_NO_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 계량기 입니다."),
	CONSUMER_NAME_SHOUD_NOT_NULL(BAD_REQUEST, "고객번호를 입력해 주세요"),

	// Modem
	MODEM_NOT_EXIST(BAD_REQUEST, "단말기 정보를 찾을 수 없습니다."),
	BAD_MODEM_NUMBE(BAD_REQUEST, "잘못된 단말기 번호 입니다."),
	BAD_IMEI_NUMBE(BAD_REQUEST, "잘못된 IMEI 번호 입니다."),
	MODEM_NO_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 단말기번호 입니다."),
	IMEI_ALREADY_EXIST(BAD_REQUEST, "이미 존재하는 IMEI 입니다."),

	// Install
	NOT_FOUND_FILE_INFO(BAD_REQUEST, "설치사진이 존재하지 않습니다."),
	NOT_FOUND_INSTALL_INFO(BAD_REQUEST, "설치정보를 찾을 수 없습니다."),
	ALREADY_INSTALLED_MODEM(BAD_REQUEST, "이미 설치된 단말기 입니다."),
	IMAGE_SIZE_NOT_ALLOW(BAD_REQUEST, "이미지는 최대 3개만 업로드 할 수 있습니다."),
	IN_CORRECT_WORKTIME(BAD_REQUEST, "유효하지 않은 작업 시간 입니다."),
	NOT_INSTALLED_MODEM(BAD_REQUEST, "설치되지 않은 단말기 입니다.");

	private final HttpStatus httpStatus;
	private final String errorMessage;
}
