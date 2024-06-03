package com.install.global.security.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.install.domain.member.application.repository.MemberRepository;
import com.install.domain.member.entity.Member;
import com.install.global.security.service.CustomUserDetails;
import com.install.global.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final MemberRepository memberRepository;
  private final JwtService jwtService;
  private final ObjectMapper objectMapper;

  @Transactional
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    // accessToken , refreshToken 생성
    Member member = getMember(userDetails);
    String accessToken = jwtService.createAccessToken(member.getEmail(), userDetails.getAuthoritiesStr(), member.getId());
    String refreshToken = jwtService.createRefreshToken(member.getEmail());
    member.updateRefreshToken(refreshToken);

    response.setContentType(APPLICATION_JSON_VALUE);
    objectMapper.writeValue(response.getWriter(), new TokenResponseDto(accessToken, refreshToken));
  }

  private Member getMember(CustomUserDetails userDetails) {
    return memberRepository.findByEmail(userDetails.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("가입된 이메일이 존재하지 않습니다."));
  }

  @Getter
  @AllArgsConstructor
  private static class TokenResponseDto {

    private String accessToken;
    private String refreshToken;
  }
}
