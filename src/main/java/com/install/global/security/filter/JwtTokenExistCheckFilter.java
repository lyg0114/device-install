package com.install.global.security.filter;

import static com.install.global.exception.CustomErrorCode.*;
import static com.install.global.security.filter.NoNeedAuthentication.*;
import static com.install.global.security.service.JwtService.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.global.exception.CustomErrorCode;
import com.install.global.exception.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *  - JWT 토큰 존재 여부 검증 필터
 */
@Slf4j
public class JwtTokenExistCheckFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	public JwtTokenExistCheckFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return isNoNeedAuthenticationURL(request);
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {

		if (!hasToken(request)) {
			log.error("url: {} | errorCode: {} | errorMessage: {} ",
				request.getRequestURL(), TOKEN_NOT_EXIST, TOKEN_NOT_EXIST.getErrorMessage());

			CustomErrorCode errorCode = TOKEN_NOT_EXIST;
			response.setStatus(errorCode.getHttpStatus().value());
			response.setContentType(APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("utf-8");
			ErrorResponse errorResponse = new ErrorResponse(errorCode);
			objectMapper.writeValue(response.getWriter(), errorResponse);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean hasToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		return authorizationHeader != null && authorizationHeader.startsWith(TOKEN_HEADER_PREFIX);
	}
}
