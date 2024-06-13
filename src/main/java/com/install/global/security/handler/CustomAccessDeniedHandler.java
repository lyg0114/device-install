package com.install.global.security.handler;

import static com.install.global.exception.CustomErrorCode.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.global.exception.CustomException;
import com.install.global.exception.ErrorResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
		throws IOException, ServletException {

		log.error("[AccessDeniedException] url: {} | errorCode: {} | errorMessage: {} | cause Exception: ",
			request.getRequestURL(), ACCESS_DENIED, ex.getMessage(), ex);

		response.setStatus(ACCESS_DENIED.getHttpStatus().value());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");

		objectMapper.writeValue(
			response.getWriter(),
			new ErrorResponse(new CustomException(ACCESS_DENIED))
		);
	}
}
