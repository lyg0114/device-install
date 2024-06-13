package com.install.global.security.filter;

import static com.install.global.security.filter.NoNeedAuthentication.*;
import static com.install.global.security.service.JwtService.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.global.exception.CustomErrorCode;
import com.install.global.exception.ErrorResponse;
import com.install.global.security.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *  - JWT 토큰으로 권한을 체크하는 필터
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final ObjectMapper objectMapper;

	/**
	 * @param request current HTTP request
	 * @return
	 *  - 인증이 필요없는 URL 설정
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return isNoNeedAuthenticationURL(request);
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 *
	 *  - JWT 토큰 유효성 검증 필터
	 */
	@Override
	protected void doFilterInternal(
		HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
	) throws ServletException, IOException {

		String authorizationHeader = request.getHeader(AUTHORIZATION);
		CustomErrorCode errorCode = null;

		try {
			String accessToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
			DecodedJWT decodedJWT = jwtService.verifyToken(accessToken);

			String email = decodedJWT.getSubject();
			String roleListStr = decodedJWT.getClaim(CLAIM_ROLE).asString();

			// SecurityContextHolder에 accessToken 포함하여 저장
			Authentication authentication = new UsernamePasswordAuthenticationToken(email, accessToken,
				getAuthorities(roleListStr));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);

		} catch (TokenExpiredException e) {
			// Access Token 만료
			errorCode = CustomErrorCode.ACCESS_TOKEN_EXPIRED;
		} catch (Exception e) {
			// 유효하지 않은 Access Token
			errorCode = CustomErrorCode.INVALID_TOKEN;
		}

		if (errorCode != null) {
			log.error("url: {} | errorCode: {} | errorMessage: {} ",
				request.getRequestURL(), errorCode, errorCode.getErrorMessage());
			response.setStatus(errorCode.getHttpStatus().value());
			response.setContentType(APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("utf-8");
			ErrorResponse errorResponse = new ErrorResponse(errorCode);
			objectMapper.writeValue(response.getWriter(), errorResponse);
		}
	}

	private List<SimpleGrantedAuthority> getAuthorities(String roleListStr) {
		String[] roles = roleListStr.split(",");
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
