package com.install.domain.metering.service;

import static com.install.domain.code.entity.CodeSet.MODEM_STAUTS;
import static com.install.domain.code.entity.CodeSet.MODEM_TYPE;
import static com.install.domain.code.entity.CodeSet.RECEIVING_DATA_SUCCESS;
import static com.install.domain.code.entity.CodeSet.getAllCodes;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto.InstallRequest;
import com.install.domain.install.service.InstallService;
import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.metering.dto.MeteringDto.MeteringResponse;
import com.install.domain.metering.dto.MeteringDto.MeteringSearchCondition;
import com.install.domain.metering.entity.MeterInfo;
import com.install.domain.metering.entity.repository.MeterInfoRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.security.service.JwtService;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
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

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.metering.service
 * @since : 11.06.24
 */
@DisplayName("검침정보 조회 테스트")
@Transactional
@SpringBootTest
class MeteringQueryServiceTest {

  @Autowired InstallService installService;
  @Autowired ModemRepository modemRepository;
  @Autowired ConsumerRepository consumerRepository;
  @Autowired CodeRepository codeRepository;
  @Autowired EntityManager em;
  @Autowired MemberRepository memberRepository;
  @MockBean JwtService jwtService;

  @Autowired MeteringService meteringService;
  @Autowired MeterInfoRepository meterInfoRepository;

  @BeforeEach void before() {
    createCodes();
  }

  @Test
  void 검침정보_조회에_성공한다() {
    //given
    saveInfo();

    em.flush();
    em.clear();

    MeteringSearchCondition condition = MeteringSearchCondition.builder().build();
    PageRequest pageable = PageRequest.of(0, 10);

    //when
    Page<MeteringResponse> meteringResponses = meteringService.searchMeterInfo(condition, pageable);

    //then
    MeteringResponse meteringResponse = meteringResponses.getContent().get(0);
    assertThat(meteringResponse.getModemNo()).isEqualTo("modemNo-modem1");
    assertThat(meteringResponse.getMeteringStateCd().getCode()).isEqualTo(RECEIVING_DATA_SUCCESS.getCode());
    assertThat(meteringResponse.getMeteringStateCd().getName()).isEqualTo("수신");
  }

  @Test
  void 기준일_검색조건으로_검침정보_조회에_성공한다() {
    //given
    saveInfo();

    em.flush();
    em.clear();

    LocalDateTime installNow = LocalDateTime.now().minusDays(2L);
    MeteringSearchCondition condition = MeteringSearchCondition.builder()
        .standardDate(installNow)
        .build();
    PageRequest pageable = PageRequest.of(0, 10);

    //when
    Page<MeteringResponse> meteringResponses = meteringService.searchMeterInfo(condition, pageable);

    //then
    assertThat(meteringResponses.getTotalElements()).isEqualTo(3L);
  }

  private void saveInfo() {
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());

    Modem modem1 = modemRepository.save(createModem("modem1"));
    Modem modem2 = modemRepository.save(createModem("modem2"));
    Modem modem3 = modemRepository.save(createModem("modem3"));
    Modem modem4 = modemRepository.save(createModem("modem4"));
    Modem modem5 = modemRepository.save(createModem("modem5"));
    Consumer consumer1 = consumerRepository.save(createConsumer("consumer1"));
    Consumer consumer2 = consumerRepository.save(createConsumer("consumer2"));
    Consumer consumer3 = consumerRepository.save(createConsumer("consumer3"));
    Consumer consumer4 = consumerRepository.save(createConsumer("consumer4"));
    Consumer consumer5 = consumerRepository.save(createConsumer("consumer5"));

    LocalDateTime installNow = LocalDateTime.now().minusDays(3L); // 3일전 설치
    installModem(modem1, consumer1, of(createMockFile("설치 성공")), installNow);
    installModem(modem2, consumer2, of(createMockFile("설치 성공")), installNow);
    installModem(modem3, consumer3, of(createMockFile("설치 성공")), installNow);
    installModem(modem4, consumer4, of(createMockFile("설치 성공")), installNow);
    installModem(modem5, consumer5, of(createMockFile("설치 성공")), installNow);

    // 설치 후 3시간 뒤 데이터 수신
    saveMeterInfo(modem1, installNow.plusHours(3L), new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(11.2));
    // 설치 후 1시간 뒤 데이터 수신
    saveMeterInfo(modem2, installNow.plusHours(1L), new BigDecimal(120.0), new BigDecimal(0.0), new BigDecimal(13.2));
    // 설치 후 2시간 뒤 데이터 수신
    saveMeterInfo(modem3, installNow.plusHours(2L), new BigDecimal(1432.0), new BigDecimal(0.0), new BigDecimal(15.2));
  }

  private void saveMeterInfo(Modem modem, LocalDateTime meteringDate, BigDecimal meteringData, BigDecimal meteringUsage, BigDecimal meteringTemp) {
    if (meteringDate.isAfter(LocalDateTime.now())) {
      return;
    }

    MeterInfo meterInfo = MeterInfo.builder()
        .modem(modem)
        .meteringDate(meteringDate)
        .meteringData(meteringData)
        .meteringUsage(meteringUsage)
        .meteringTemp(meteringTemp)
        .meteringStateCd(RECEIVING_DATA_SUCCESS.getCodeEntity())
        .build();

    meterInfoRepository.save(meterInfo);
    BigDecimal randomUsage = createRandomValue(new BigDecimal("0.005"), new BigDecimal("0.020"));
    saveMeterInfo(modem, meteringDate.plusHours(1L), meteringData.add(randomUsage), randomUsage, meteringTemp);
  }

  // 정확한 연산을 위해 BigDecimal 사용
  // min ~ max 사이의 랜덤 값을 생성
  private BigDecimal createRandomValue(BigDecimal min, BigDecimal max) {
    int SCALE = 3; // 소수점 이하 3자리
    Random random = new Random();
    BigDecimal randomBigDecimal = new BigDecimal(random.nextDouble());
    BigDecimal range = max.subtract(min);
    BigDecimal scaledRandomValue = range.multiply(randomBigDecimal).setScale(SCALE, RoundingMode.HALF_UP);
    BigDecimal randomValue = min.add(scaledRandomValue);
    return randomValue;
  }

  public void installModem(
      Modem modem, Consumer consumer, List<MultipartFile> installImages, LocalDateTime workTime
  ) {
    InstallRequest installSuccess = InstallRequest.builder()
        .workTime(workTime)
        .comment("설치 성공").build();
    installService.installModem(modem.getId(), consumer.getId(), installSuccess, installImages);
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