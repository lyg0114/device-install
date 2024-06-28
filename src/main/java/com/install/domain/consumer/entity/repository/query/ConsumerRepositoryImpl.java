package com.install.domain.consumer.entity.repository.query;

import static com.install.domain.consumer.entity.QConsumer.*;
import static com.install.domain.modem.entity.QModem.*;
import static java.util.Objects.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.install.domain.code.entity.QCode;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerRequest;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.consumer.entity.Consumer;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository
 * @since : 09.06.24
 */
@RequiredArgsConstructor
public class ConsumerRepositoryImpl implements ConsumerRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;
	private final JPAQueryFactory queryFactory;
	private final QCode code1 = new QCode("code1");
	private final QCode code2 = new QCode("code2");

	@Override
	public Page<Consumer> searchConsumer(ConsumerSearchCondition condition, Pageable pageable) {
		return PageableExecutionUtils
			.getPage(
				queryFactory
					.selectFrom(consumer)
					.leftJoin(consumer.installedModem, modem).fetchJoin()
					.leftJoin(modem.modemTypeCd, code1).fetchJoin()
					.leftJoin(modem.modemStatusCd, code2).fetchJoin()
					.where(
						modemNoEq(condition.getModemNo()),
						consumerNoEq(condition.getConsumerNo()),
						meterNoEq(condition.getMeterNo()),
						installDateBetween(condition.getFrom(), condition.getTo())
					)
					.offset(pageable.getOffset())
					.limit(pageable.getPageSize())
					.fetch(),
				pageable,
				() -> queryFactory
					.select(consumer.count())
					.from(consumer)
					.leftJoin(consumer.installedModem, modem).fetchJoin()
					.leftJoin(modem.modemTypeCd, code1).fetchJoin()
					.leftJoin(modem.modemStatusCd, code2).fetchJoin()
					.where(
						modemNoEq(condition.getModemNo()),
						consumerNoEq(condition.getConsumerNo()),
						meterNoEq(condition.getMeterNo()),
						installDateBetween(condition.getFrom(), condition.getTo())
					)
					.fetchOne()
			);
	}

	private BooleanExpression installDateBetween(LocalDateTime from, LocalDateTime to) {
		if (isNull(from) || isNull(to) || from.isAfter(to)) {
			return null;
		}
		return consumer.installDate.between(from, to);
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

	@Override
	public void bulkInsert(List<ConsumerRequest> requests) {
		String sql = "INSERT INTO consumer (created_at, consumer_no, consumer_nm, meter_no, city, street, zipcode) "
			+ "VALUES (?,?,?,?,?,?,?)";

		LocalDateTime now = LocalDateTime.now();
		jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, consumerRequest) -> {
			ps.setString(1, now.toString());
			ps.setString(2, consumerRequest.getConsumerNo());
			ps.setString(3, consumerRequest.getConsumerName());
			ps.setString(4, consumerRequest.getMeterNo());
			ps.setString(5, consumerRequest.getCity());
			ps.setString(6, consumerRequest.getStreet());
			ps.setString(7, consumerRequest.getZipcode());
		});
	}
}
