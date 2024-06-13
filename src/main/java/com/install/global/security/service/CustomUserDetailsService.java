package com.install.global.security.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.Role;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.member.entity.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final RoleRepository roleRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("가입된 이메일이 존재하지 않습니다."));

		List<Role> roles = roleRepository.findRolesByMemberId(member.getId());

		return new CustomUserDetails(member, roles);
	}
}
