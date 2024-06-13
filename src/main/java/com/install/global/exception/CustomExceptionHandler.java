package com.install.global.exception;

import static com.install.global.exception.CustomErrorCode.*;

import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex, HttpServletRequest request) {

		log.error("[CustomException] url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
			request.getRequestURL(), ex.getErrorCode(), ex.getErrorMessage(), ex.getCause());

		return ResponseEntity
			.status(ex.getErrorCode().getHttpStatus())
			.body(new ErrorResponse(ex));

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
		HttpServletRequest request) {

		String validationMessage = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
		log.error("[MethodArgumentNotValidException] url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
			request.getRequestURL(), INVALID_VALUE, validationMessage, ex);

		CustomException customException = new CustomException(INVALID_VALUE, validationMessage);
		return ResponseEntity
			.status(INVALID_VALUE.getHttpStatus())
			.body(new ErrorResponse(customException));

	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex,
		HttpServletRequest request) {

		log.error("[NoResourceFoundException] url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
			request.getRequestURL(), INVALID_HTTP_METHOD, INVALID_HTTP_METHOD.getErrorMessage(), ex);

		CustomException customException = new CustomException(INVALID_HTTP_METHOD);
		return ResponseEntity
			.status(INVALID_VALUE.getHttpStatus())
			.body(new ErrorResponse(customException));

	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
		HttpServletRequest request) {

		log.error("[AccessDeniedException] url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
			request.getRequestURL(), ACCESS_DENIED, ACCESS_DENIED.getErrorMessage(), ex);

		CustomException customException = new CustomException(ACCESS_DENIED);
		return ResponseEntity
			.status(INVALID_VALUE.getHttpStatus())
			.body(new ErrorResponse(customException));

	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
		HttpServletRequest request) {

		log.error("[DataIntegrityViolationException Exception] url: {} | errorMessage: {} | Exception ex: {}",
			request.getRequestURL(), ex.getMessage(), ex.getClass().getName());

		CustomException customException = new CustomException(INVALID_VALUE, "입력 값이 잘못 되었습니다.");
		return ResponseEntity
			.status(INVALID_VALUE.getHttpStatus())
			.body(new ErrorResponse(customException));

	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exception(Exception ex, HttpServletRequest request) {

		log.error("[Common Exception] url: {} | errorMessage: {} | Exception ex: {}",
			request.getRequestURL(), ex.getMessage(), ex.getClass().getName());

		return ResponseEntity
			.status(INVALID_VALUE.getHttpStatus())
			.body(new ErrorResponse(UNKNON_INVALID_VALUE));

	}
}
