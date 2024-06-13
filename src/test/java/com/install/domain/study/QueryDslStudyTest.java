package com.install.domain.study;

import static com.install.domain.code.entity.CodeSet.*;
import static com.install.domain.consumer.entity.QConsumer.*;
import static com.install.domain.modem.entity.QModem.*;
import static java.util.List.*;
import static java.util.Objects.isNull;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.QCode;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto.InstallRequest;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.domain.install.service.InstallService;
import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.security.service.JwtService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity.repository
 * @since : 06.06.24
 */
@Transactional
@SpringBootTest
class QueryDslStudyTest {

	@Autowired InstallRepository installRepository;
	@Autowired InstallService installService;
	@Autowired ModemRepository modemRepository;
	@Autowired ConsumerRepository consumerRepository;
	@Autowired CodeRepository codeRepository;
	@Autowired EntityManager em;
	@Autowired JPAQueryFactory queryFactory;
	@MockBean JwtService jwtService;
	@Autowired MemberRepository memberRepository;

	@BeforeEach
	void before() {
		createCodes();
	}

	@Test
	void left_join_test() {
		//given
		when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
		// modem, 고객 정보 저장
		Modem modem1 = modemRepository.save(createModem("modem1"));
		Modem modem1_1 = modemRepository.save(createModem("modem1_1"));
		Modem modem2 = modemRepository.save(createModem("modem2"));
		Modem modem2_1 = modemRepository.save(createModem("modem2_1"));
		Modem modem3 = modemRepository.save(createModem("modem3"));
		Consumer consumer1 = consumerRepository.save(createConsumer("consumer1"));
		Consumer consumer2 = consumerRepository.save(createConsumer("consumer2"));
		Consumer consumer3 = consumerRepository.save(createConsumer("consumer3"));
		Consumer consumer4 = consumerRepository.save(createConsumer("consumer4"));
		Consumer consumer5 = consumerRepository.save(createConsumer("consumer5"));

		// 설치정보 저장
		LocalDateTime now = LocalDateTime.now();
		installModem(modem1, consumer1, of(createMockFile("설치 성공")), now.minusDays(6L));
		demolishModem(modem1, of(createMockFile("철거 성공")), now.minusDays(5L));
		installModem(modem1_1, consumer1, of(createMockFile("설치 성공")), now.minusDays(4L));
		demolishModem(modem1_1, of(createMockFile("철거 성공")), now.minusDays(3L));
		installModem(modem2, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
		demolishModem(modem2, of(createMockFile("철거 성공")), now.minusDays(5L));
		installModem(modem2_1, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
		installModem(modem3, consumer3, of(createMockFile("설치 성공")), now.minusDays(6L));

		ConsumerSearchCondition condition = ConsumerSearchCondition.builder().build();
		PageRequest pageRequest = PageRequest.of(0, 10);

		//when
		// code의 alias가 중복되어선 안된다.
		QCode code1 = new QCode("code1");
		QCode code2 = new QCode("code2");
		List<Consumer> consumers = queryFactory
			.select(consumer)
			.from(consumer)
			.leftJoin(consumer.installedModem, modem).fetchJoin() // fetchJoin으로 Lazy 로딩 방지
			.leftJoin(modem.modemTypeCd, code1).fetchJoin() // fetchJoin으로 Lazy 로딩 방지
			.leftJoin(modem.modemStatusCd, code2).fetchJoin() // fetchJoin으로 Lazy 로딩 방지
			.where(
				modemNoEq(condition.getModemNo()),
				consumerNoEq(condition.getConsumerNo()),
				meterNoEq(condition.getMeterNo())
			)
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		//then
		for (Consumer consumer : consumers) {
			System.out.println("===========================================================================");
			System.out.println("fetch1 = " + consumer);
		}
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

	public void installModem(
		Modem modem, Consumer consumer, List<MultipartFile> installImages, LocalDateTime workTime
	) {
		InstallRequest installSuccess = InstallRequest.builder()
			.workTime(workTime)
			.comment("설치 성공").build();
		installService.installModem(modem.getId(), consumer.getId(), installSuccess, installImages);
	}

	public void demolishModem(Modem modem, List<MultipartFile> demolishImages, LocalDateTime workTime) {
		InstallRequest demolishSuccess = InstallRequest.builder()
			.workTime(workTime)
			.comment("철거 성공").build();
		installService.demolishModem(modem.getId(), demolishSuccess, demolishImages);
	}

	public void maintenanceModem(Modem modem, List<MultipartFile> maintenanceImages, LocalDateTime workTime) {
		InstallRequest maintenanceSuccess = InstallRequest.builder()
			.workTime(workTime)
			.comment("유지보수 성공").build();
		installService.maintenanceModem(modem.getId(), maintenanceSuccess, maintenanceImages);
	}

	private void createCodes() {
		codeRepository.saveAll(getAllCodes());
	}

	private Consumer createConsumer(String str) {
		return Consumer.builder()
			.consumerNo("consumerNo-" + str)
			.consumerName("consumerName-" + str)
			.meterNo("meterNo-" + str)
			.address(Address.builder()
				.street("street-" + str)
				.city("city-" + str)
				.zipcode("zipcode-" + str)
				.build())
			.location(Location.builder()
				.geoX("getX-" + str)
				.geoY("getY-" + str)
				.build())
			.build();
	}

	private Modem createModem(String str) {
		return Modem.builder()
			.modemNo("modemNo-" + str)
			.imei("imei-" + str)
			.buildCompany("comapnty-" + str)
			.modemTypeCd(Code.builder().code(MODEM_TYPE.getCode()).build())
			.modemStatusCd(Code.builder().code(MODEM_STAUTS.getCode()).build())
			.build();
	}

	private MockMultipartFile createMockFile(String content) {
		return new MockMultipartFile("foo", "foo.txt",
			MediaType.TEXT_PLAIN_VALUE, content.getBytes());
	}

	private Member createMember(String name) {
		return Member.builder()
			.name(name)
			.nickname("에이스")
			.email("worker@example.com")
			.password("1234")
			.build();
	}
}