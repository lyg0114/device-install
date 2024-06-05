package com.install.domain.code.entity.repository;

import com.install.domain.code.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.code.entity.repository
 * @since : 05.06.24
 */
public interface CodeRepository extends JpaRepository<Code, Long> {

}
