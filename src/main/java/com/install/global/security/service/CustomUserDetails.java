package com.install.global.security.service;

import static com.install.global.exception.CustomErrorCode.*;
import static java.util.Objects.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.Role;
import com.install.global.exception.CustomException;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

	private final Member member;
	private final List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
	private final String roleStrs;

	public CustomUserDetails(Member member, List<Role> roles) {

		this.member = member;

		if (isNull(roles) || roles.isEmpty()) {
			throw new CustomException(USER_NOT_HAVE_ROLE);
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < roles.size(); i++) {
			this.grantedAuthorities.add(new SimpleGrantedAuthority(roles.get(i).getRoleName()));
			sb.append(roles.get(i).getRoleName());
			if (i < roles.size() - 1) {
				sb.append(",");
			}
		}

		this.roleStrs = sb.toString();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return grantedAuthorities;
	}

	public String getAuthoritiesStr() {
		return roleStrs;
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
