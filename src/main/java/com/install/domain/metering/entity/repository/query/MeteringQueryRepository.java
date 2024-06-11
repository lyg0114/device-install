package com.install.domain.metering.entity.repository.query;

import static com.install.domain.consumer.entity.QConsumer.consumer;
import static com.install.domain.metering.entity.QMeterInfo.meterInfo;
import static com.install.domain.modem.entity.QModem.modem;
import static java.util.Objects.isNull;

import com.install.domain.metering.dto.MeteringDto.MeteringSearchCondition;
import com.install.domain.metering.entity.MeterInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.entity.repository
 * @since : 11.06.24
 */
@RequiredArgsConstructor
@Repository
public class MeteringQueryRepository {

  private final JPAQueryFactory queryFactory;

  public Page<MeterInfo> searchMeterInfo(MeteringSearchCondition condition, Pageable pageable) {
    return PageableExecutionUtils
        .getPage(
            queryFactory
                .selectFrom(meterInfo)
                .leftJoin(meterInfo.modem, modem).fetchJoin()
                .leftJoin(modem.installedConsumer, consumer).fetchJoin()
                .where(
                    meteringDateBetween(condition.getStandardDate()),
                    modemNoEq(condition.getModemNo()),
                    consumerNoEq(condition.getConsumerNo())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(),
            pageable,
            () -> queryFactory
                .select(meterInfo.count())
                .from(meterInfo)
                .leftJoin(meterInfo.modem, modem).fetchJoin()
                .leftJoin(modem.installedConsumer, consumer).fetchJoin()
                .where(
                    meteringDateBetween(condition.getStandardDate()),
                    modemNoEq(condition.getModemNo()),
                    consumerNoEq(condition.getConsumerNo())
                )
                .fetchOne()
        );
  }

  /**
   * 기준시간 1시간전 ~ 기준시간
   */
  private BooleanExpression meteringDateBetween(LocalDateTime stanDardDate) {
    if (isNull(stanDardDate)) {
      return null;
    }
    return meterInfo.meteringDate.between(stanDardDate.minusHours(1L), stanDardDate);
  }

  private BooleanExpression modemNoEq(String modemNo) {
    return !isNull(modemNo) ? meterInfo.modem.modemNo.eq(modemNo) : null;
  }

  private BooleanExpression consumerNoEq(String consumerNo) {
    return !isNull(consumerNo) ? meterInfo.modem.installedConsumer.consumerNo.eq(consumerNo) : null;
  }
}
