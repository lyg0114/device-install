package com.install.domain.code.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.install.domain.code.entity.Code;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.code.entity.repository
 * @since : 05.06.24
 */
public interface CodeRepository extends JpaRepository<Code, Long> {

}
