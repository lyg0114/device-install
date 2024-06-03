package com.install.domain.member.application.repository;


import com.install.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByEmail(String email);

  Boolean existsByNickname(String nickname);

  Optional<Member> findByEmail(String username);
}
