package com.install.global.exception;

import lombok.Getter;

/**
 * @author : iyeong-gyo
 * @package : com.apipractice.global.exception
 * @since : 18.05.24
 */
@Getter
public class CustomException extends RuntimeException {

  private final CustomErrorCode errorCode;
  private final String errorMessage;

  /**
   * @param errorCode
   * - cause 없이 생성
   */
  public CustomException(CustomErrorCode errorCode) {
    super(errorCode.getErrorMessage());
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getErrorMessage();
  }

  public CustomException(CustomErrorCode errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * @param errorCode
   * @param cause
   *  - 로깅을 위해 실제 원인이 되는 cause와 함께 생성
   */
  public CustomException(CustomErrorCode errorCode, Exception cause) {
    super(errorCode.getErrorMessage(), cause);
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getErrorMessage();
  }

  public CustomException(CustomErrorCode errorCode, String errorMessage, Exception cause) {
    super(errorMessage, cause);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
