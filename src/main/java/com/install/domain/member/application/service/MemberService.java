package com.install.domain.member.application.service;

import static com.install.global.exception.CustomErrorCode.EMAIL_ALREADY_EXIST;
import static com.install.global.exception.CustomErrorCode.NICKNAME_ALREADY_EXIST;
import static org.springframework.util.StringUtils.hasText;

import com.install.domain.member.application.repository.MemberRepository;
import com.install.domain.member.dto.MemberDto;
import com.install.domain.member.dto.MemberDto.SignUpRequest;
import com.install.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  @Transactional
  public void signUp(MemberDto.SignUpRequest requestDto) {
    validateSignUpDto(requestDto);
    memberRepository.save(requestDto.toEntity());
  }

  private void validateSignUpDto(SignUpRequest requestDto) {
    validateDuplicateEmail(requestDto.getEmail());
    validateDuplicateNickName(requestDto.getNickname());
  }

  private void validateDuplicateEmail(String email) {
    if (hasText(email) && memberRepository.existsByEmail(email)) {
      throw new CustomException(EMAIL_ALREADY_EXIST);
    }
  }

  private void validateDuplicateNickName(String nickname) {
    if (hasText(nickname) && memberRepository.existsByNickname(nickname)) {
      throw new CustomException(NICKNAME_ALREADY_EXIST);
    }
  }
}
