package com.install.domain.member.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.install.domain.member.entity.MemberRole;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {

}
