package com.install.domain.study;

import static com.install.domain.code.entity.CodeSet.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.modem.dto.ModemDto.ModemRequest;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;

import jakarta.persistence.EntityManager;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 13.06.24
 */
@DisplayName("JdbcTemplate 학습 테스트")
@Transactional
@SpringBootTest
class JdbcTemplateTest {

	@Autowired JdbcTemplate jdbcTemplate;
	@Autowired CodeRepository codeRepository;
	@Autowired ModemRepository modemRepository;
	@Autowired EntityManager em;

	private int totalCount = 10000;
	private int batchSize = 1000;

	@BeforeEach
	void before() {
		createCodes();
	}

	private void createCodes() {
		codeRepository.saveAll(getAllCodes());
		em.flush();
		em.clear();
	}

	@Test
	void JdbcTemplate_벌크연산테스트() {
		List<ModemRequest> requests = getModemRequests();
		long startTime = System.nanoTime();

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

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		double seconds = (double)duration / 1_000_000_000.0;
		System.out.println("bulk_isert_JdbcTemplate - 실행 시간: " + seconds + " 초");
	}

	@Test
	void JdbcTemplate_batchsize_벌크연산테스트() {
		List<ModemRequest> requests = getModemRequests();
		long startTime = System.nanoTime();
		String sql = "INSERT INTO modem (created_at, modem_no, imei, build_company, modem_status_cd, modem_type_cd) "
			+ "VALUES (?,?,?,?,?,?)";

		LocalDateTime now = LocalDateTime.now();
		int totalBatches = (int)Math.ceil((double)requests.size() / batchSize);

		for (int i = 0; i < totalBatches; i++) {
			List<ModemRequest> batchList = requests.subList(i * batchSize, Math.min(i * batchSize + batchSize, requests.size()));
			jdbcTemplate.batchUpdate(sql, batchList, batchSize, (ps, modemRequest) -> {
				ps.setString(1, now.toString());
				ps.setString(2, modemRequest.getModemNo());
				ps.setString(3, modemRequest.getImei());
				ps.setString(4, modemRequest.getBuildCompany());
				ps.setString(5, modemRequest.getModemStatusCd());
				ps.setString(6, modemRequest.getModemTypeCd());
			});
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		double seconds = (double)duration / 1_000_000_000.0;
		System.out.println("bulk_insert_JdbcTemplate_batchsize - 실행 시간: " + seconds + " 초");
	}

	@Test
	void hibernate_벌크연산테스트() {
		List<Modem> requests = new ArrayList<>();
		for (int i = 0; i < totalCount; i++) {
			requests.add(Modem.builder()
				.modemNo("modemNo-" + i)
				.imei("imei-" + i)
				.buildCompany("buildCompany-" + i)
				.modemStatusCd(Code.builder().code(MODEM_STAUTS_NORMAL.getCode()).build())
				.modemTypeCd(Code.builder().code(MODEM_TYPE_LORA.getCode()).build())
				.build());
		}
		modemRepository.saveAll(requests);
	}

	private List<ModemRequest> getModemRequests() {
		List<ModemRequest> requests = new ArrayList<>();
		for (int i = 0; i < totalCount; i++) {
			requests.add(ModemRequest.builder()
				.modemNo("modemNo-" + i)
				.imei("imei-" + i)
				.buildCompany("buildCompany-" + i)
				.modemStatusCd(MODEM_STAUTS_NORMAL.getCode())
				.modemTypeCd(MODEM_TYPE_LORA.getCode())
				.build());
		}
		return requests;
	}
}