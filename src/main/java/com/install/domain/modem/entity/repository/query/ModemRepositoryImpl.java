package com.install.domain.modem.entity.repository.query;

import static com.install.domain.consumer.entity.QConsumer.*;
import static com.install.domain.modem.entity.QModem.*;
import static java.util.Objects.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.install.domain.code.entity.QCode;
import com.install.domain.modem.dto.ModemDto;
import com.install.domain.modem.dto.ModemDto.ModemInstallCount;
import com.install.domain.modem.dto.ModemDto.ModemSearchCondition;
import com.install.domain.modem.entity.Modem;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.entity.repository.query
 * @since : 11.06.24
 */
@RequiredArgsConstructor
public class ModemRepositoryImpl implements ModemRepositoryCustom {

	private final JdbcTemplate jdbcTemplate;
	private final JPAQueryFactory queryFactory;
	private final QCode code1 = new QCode("code1");
	private final QCode code2 = new QCode("code2");

	@Override
	public Page<Modem> searchModems(ModemSearchCondition condition, Pageable pageable) {
		return PageableExecutionUtils
			.getPage(
				queryFactory
					.select(modem)
					.from(modem)
					.leftJoin(modem.installedConsumer, consumer).fetchJoin()
					.leftJoin(modem.modemTypeCd, code1).fetchJoin()
					.leftJoin(modem.modemStatusCd, code2).fetchJoin()
					.where(
						modemNoEq(condition.getModemNo()),
						consumerNoEq(condition.getConsumerNo())
					)
					.offset(pageable.getOffset())
					.limit(pageable.getPageSize())
					.fetch(),
				pageable,
				() -> queryFactory
					.select(modem.count())
					.from(modem)
					.leftJoin(modem.installedConsumer, consumer).fetchJoin()
					.leftJoin(modem.modemTypeCd, code1).fetchJoin()
					.leftJoin(modem.modemStatusCd, code2).fetchJoin()
					.where(
						modemNoEq(condition.getModemNo()),
						consumerNoEq(condition.getConsumerNo())
					)
					.fetchOne()
			);
	}

	@Override
	public ModemInstallCount modeminstallCount() {
		return ModemInstallCount.builder()
			.totalCount(getTotalCount())
			.installedCount(installedCount(true))
			.uninstalledCount(installedCount(false))
			.build();
	}

	@Override
	public void bulkInsertModem(List<ModemDto.ModemRequest> requests) {
		String sql = "INSERT INTO modem (created_at, modem_no, imei, build_company, modem_status_cd, modem_type_cd) "
			+ "VALUES (?,?,?,?,?,?)";

		LocalDateTime now = LocalDateTime.now();
		jdbcTemplate.batchUpdate(sql, requests, requests.size(), (ps, modemRequest) -> {
			ps.setString(1, now.toString());
			ps.setString(2, modemRequest.getModemNo());
			ps.setString(3, modemRequest.getImei());
			ps.setString(4, modemRequest.getBuildCompany());
			ps.setString(5, modemRequest.getModemStatusCd());
			ps.setString(6, modemRequest.getModemTypeCd());
		});
	}

	private Long installedCount(Boolean isInstalled) {
		return queryFactory
			.select(modem.count())
			.from(modem)
			.leftJoin(modem.installedConsumer, consumer)
			.where(isInstalled ? modem.installedConsumer.isNotNull() : modem.installedConsumer.isNull())
			.fetchOne();
	}

	private Long getTotalCount() {
		return queryFactory
			.select(modem.count())
			.from(modem)
			.fetchOne();
	}

	private BooleanExpression modemNoEq(String modemNo) {
		return !isNull(modemNo) ? modem.modemNo.eq(modemNo) : null;
	}

	private BooleanExpression consumerNoEq(String consumerNo) {
		return !isNull(consumerNo) ? consumer.consumerNo.eq(consumerNo) : null;
	}
}
