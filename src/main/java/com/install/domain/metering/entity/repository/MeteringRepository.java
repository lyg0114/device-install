package com.install.domain.metering.entity.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.entity.repository
 * @since : 11.06.24
 */
@RequiredArgsConstructor
@Repository
public class MeteringRepository {

  private final JPAQueryFactory queryFactory;
}
