package com.install.global.security.handler;

import static com.install.global.exception.CustomErrorCode.INVALID_VALUE;
import static com.install.global.exception.CustomErrorCode.LOGIN_FAILED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.global.exception.CustomException;
import com.install.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException ex
  ) throws IOException {
    log.error( "[AuthenticationException] url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
        request.getRequestURL(), INVALID_VALUE, ex.getMessage(), ex);

    response.setStatus(LOGIN_FAILED.getHttpStatus().value());
    response.setContentType(APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("utf-8");

    objectMapper.writeValue(
        response.getWriter(),
        new ErrorResponse(new CustomException(LOGIN_FAILED, ex.getMessage()))
    );
  }
}
