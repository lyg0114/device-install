package com.install.domain.consumer.entity.repository.query;

import static com.install.domain.code.entity.QCode.code1;
import static com.install.domain.consumer.entity.QConsumer.consumer;
import static com.install.domain.install.entity.QInstallInfo.installInfo;
import static com.install.domain.modem.entity.QModem.modem;
import static java.util.Objects.isNull;

import com.install.domain.code.entity.QCode;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.consumer.entity.Consumer;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository
 * @since : 09.06.24
 */
@RequiredArgsConstructor
public class ConsumerRepositoryImpl implements ConsumerRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private static QCode code1 = new QCode("code1");
  private static QCode code2 = new QCode("code2");

  @Override
  public Page<Consumer> searchConsumer(ConsumerSearchCondition condition, Pageable pageable) {
    return PageableExecutionUtils
        .getPage(
            queryFactory
                .select(consumer)
                .from(consumer)
                .leftJoin(consumer.installedModem, modem).fetchJoin()
                .leftJoin(modem.modemTypeCd, code1).fetchJoin()
                .leftJoin(modem.modemStatusCd, code2).fetchJoin()
                .where(
                    modemNoEq(condition.getModemNo()),
                    consumerNoEq(condition.getConsumerNo()),
                    meterNoEq(condition.getMeterNo())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(),
            pageable,
            () -> queryFactory
                .select(consumer.count())
                .from(installInfo)
                .leftJoin(consumer.installedModem, modem).fetchJoin()
                .leftJoin(modem.modemTypeCd, code1).fetchJoin()
                .leftJoin(modem.modemStatusCd, code2).fetchJoin()
                .where(
                    modemNoEq(condition.getModemNo()),
                    consumerNoEq(condition.getConsumerNo()),
                    meterNoEq(condition.getMeterNo())
                )
                .fetchOne()
        );
  }

  private BooleanExpression modemNoEq(String modemNo) {
    return !isNull(modemNo) ? consumer.installedModem.modemNo.eq(modemNo) : null;
  }

  private BooleanExpression consumerNoEq(String consumerNo) {
    return !isNull(consumerNo) ? consumer.consumerNo.eq(consumerNo) : null;
  }

  private BooleanExpression meterNoEq(String meterNo) {
    return !isNull(meterNo) ? consumer.meterNo.eq(meterNo) : null;
  }
}
