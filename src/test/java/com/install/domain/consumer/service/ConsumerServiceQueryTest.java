package com.install.domain.consumer.service;

import static com.install.domain.code.entity.CodeSet.*;
import static java.util.List.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.CodeSet;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerResponse;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerSearchCondition;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto.InstallRequest;
import com.install.domain.install.service.InstallService;
import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.security.service.JwtService;

import jakarta.persistence.EntityManager;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.entity.repository.query
 * @since : 10.06.24
 */
@DisplayName("고객정보 조회 테스트")
@Transactional
@SpringBootTest
class ConsumerServiceQueryTest {

	@Autowired ConsumerService consumerService;
	@Autowired InstallService installService;
	@Autowired ModemRepository modemRepository;
	@Autowired ConsumerRepository consumerRepository;
	@Autowired CodeRepository codeRepository;
	@Autowired EntityManager em;
	@Autowired MemberRepository memberRepository;
	@MockBean JwtService jwtService;

	@BeforeEach
	void before() {
		createCodes();
	}

	@Test
	void 고객정보_조회에_성공한다() {
		//given
		when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());

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

		LocalDateTime now = LocalDateTime.now();
		installModem(modem1, consumer1, of(createMockFile("설치 성공")), now.minusDays(6L));
		demolishModem(modem1, of(createMockFile("철거 성공")), now.minusDays(5L));
		installModem(modem1_1, consumer1, of(createMockFile("설치 성공")), now.minusDays(4L));
		demolishModem(modem1_1, of(createMockFile("철거 성공")), now.minusDays(3L));
		installModem(modem2, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
		demolishModem(modem2, of(createMockFile("철거 성공")), now.minusDays(5L));
		installModem(modem2_1, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
		installModem(modem3, consumer3, of(createMockFile("설치 성공")), now.minusDays(6L));

		em.flush();
		em.clear();

		ConsumerSearchCondition condition = ConsumerSearchCondition.builder()
			.modemNo("modemNo-modem2_1")
			.build();
		PageRequest pageRequest = PageRequest.of(0, 10);

		//when
		Page<ConsumerResponse> consumerResponses = consumerService.searchConsumers(condition, pageRequest);

		//then
		ConsumerResponse consumerResponse = consumerResponses.getContent().get(0);
		assertThat(consumerResponses.getTotalElements()).isEqualTo(1L);
		assertThat(consumerResponse.getConsumerName()).isEqualTo("consumerName-consumer2");
		assertThat(consumerResponse.getConsumerNo()).isEqualTo("consumerNo-consumer2");
		assertThat(consumerResponse.getMeterNo()).isEqualTo("meterNo-consumer2");
		assertThat(consumerResponse.getInstalledModemNo()).isEqualTo("modemNo-modem2_1");
	}

	@Test
	void 설치일로_조건검색하여_고객정보_조회에_성공한다() {
		//given
		when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
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

		LocalDateTime now = LocalDateTime.now();
		installModem(modem1, consumer1, of(createMockFile("설치 성공")), now.minusDays(6L));
		demolishModem(modem1, of(createMockFile("철거 성공")), now.minusDays(5L));
		installModem(modem1_1, consumer1, of(createMockFile("설치 성공")), now.minusDays(4L));
		installModem(modem2, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
		demolishModem(modem2, of(createMockFile("철거 성공")), now.minusDays(5L));
		installModem(modem2_1, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
		installModem(modem3, consumer3, of(createMockFile("설치 성공")), now.minusDays(6L));

		em.flush();
		em.clear();

		ConsumerSearchCondition condition = ConsumerSearchCondition.builder()
			.from(LocalDateTime.now().minusDays(7L))
			.to(LocalDateTime.now().minusDays(5L))
			.build();

		PageRequest pageRequest = PageRequest.of(0, 10);

		//when
		Page<ConsumerResponse> consumerResponses = consumerService.searchConsumers(condition, pageRequest);

		//then
		assertThat(consumerResponses.getTotalElements()).isEqualTo(2L);
	}

	public void installModem(Modem modem, Consumer consumer, List<MultipartFile> installImages, LocalDateTime workTime) {
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

	private Member createMember(String name) {
		return Member.builder()
			.name(name)
			.nickname("에이스")
			.email("worker@example.com")
			.password("1234")
			.build();
	}

	private Code createCode(CodeSet codeSet) {
		return Code.builder()
			.code(codeSet.getCode())
			.name(codeSet.getName())
			.level(codeSet.getLevel())
			.build();
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

	private MockMultipartFile createMockFile(String content) {
		MockMultipartFile sameplFile = new MockMultipartFile("foo", "foo.txt",
			MediaType.TEXT_PLAIN_VALUE, content.getBytes());
		return sameplFile;
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
}