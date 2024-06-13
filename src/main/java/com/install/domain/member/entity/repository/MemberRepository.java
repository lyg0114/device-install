package com.install.domain.member.entity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.install.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);

	Boolean existsByNickname(String nickname);

	Optional<Member> findByEmail(String username);
}
