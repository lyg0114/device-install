package com.install.domain.install.service;

import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_CHANGE;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_DEMOLISH;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_INSTALLED;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_MAINTANCE;
import static com.install.domain.code.entity.CodeSet.MODEM_STAUTS;
import static com.install.domain.code.entity.CodeSet.MODEM_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.CodeSet;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto;
import com.install.domain.install.dto.InstallDto.InstallHistoryByConsumer;
import com.install.domain.install.dto.InstallDto.InstallHistoryByModem;
import com.install.domain.install.dto.InstallDto.InstallRequest;
import com.install.domain.install.dto.InstallDto.historyInfo;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.security.service.JwtService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.service
 * @since : 05.06.24
 */
@Transactional
@SpringBootTest
class InstallServiceTest {

  @Autowired
  InstallService installService;

  @Autowired
  InstallRepository installRepository;

  @Autowired
  ModemRepository modemRepository;

  @Autowired
  ConsumerRepository consumerRepository;

  @Autowired
  CodeRepository codeRepository;

  @MockBean
  JwtService jwtService;

  @Autowired
  EntityManager em;

  @Autowired
  MemberRepository memberRepository;

  @BeforeEach
  void before() {
    createCodes();
  }

  private void createCodes() {
    codeRepository.save(createCode(MODEM_TYPE));
    codeRepository.save(createCode(MODEM_STAUTS));
    codeRepository.save(createCode(MODEM_INSTALL_STATUS));
    codeRepository.save(createCode(MODEM_INSTALL_STATUS_INSTALLED));
    codeRepository.save(createCode(MODEM_INSTALL_STATUS_CHANGE));
    codeRepository.save(createCode(MODEM_INSTALL_STATUS_MAINTANCE));
    codeRepository.save(createCode(MODEM_INSTALL_STATUS_DEMOLISH));
  }

  private Member createMember(String name) {
    Member worker = Member.builder()
        .name(name)
        .nickname("에이스")
        .email("worker@example.com")
        .password("1234")
        .build();
    return worker;
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

  private List<MultipartFile> createSampleFiles(String content, int count) {
    List<MultipartFile> sampleFiles = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      sampleFiles.add(createMockFile(new StringBuilder(content)
          .append("-")
          .append(i).toString()));
    }
    return sampleFiles;
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

  private InstallInfo createInstallInfo(Modem modem, Consumer consumer, CodeSet workTypeCode,
      String comment, LocalDateTime workTime) {
    return InstallInfo.builder()
        .modem(Modem.builder().id(modem.getId()).build())
        .consumer(Consumer.builder().id(consumer.getId()).build())
        .workTypeCd(Code.builder().code(workTypeCode.getCode()).build())
        .comment(comment)
        .workTime(workTime)
        .build();
  }

  @Test
  void 단말기_설치를_성공한다() {
    //given
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
    Modem savedModem = modemRepository.save(createModem("modem"));
    Consumer savedConsumer = consumerRepository.save(createConsumer("consumer"));
    InstallDto.InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_INSTALLED.getCode())
        .comment("install success !!")
        .build();

    em.flush();
    em.clear();

    //when
    installService.installModem(savedModem.getId(), savedConsumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.findAll().get(0);
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(requestDto.getWorkTypeCd());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(savedModem.getModemNo());
    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(savedConsumer.getConsumerNo());
    // TODO : 파일 검증 로직 추가 필요
  }

  @Test
  void 단말기_교체를_성공한다() {
    //given
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
    Modem savedModem = modemRepository.save(createModem("modem"));
    Consumer savedConsumer = consumerRepository.save(createConsumer("consumer"));
    InstallDto.InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_INSTALLED.getCode())
        .comment("신규설치 성공")
        .build();

    installService.installModem(savedModem.getId(), savedConsumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    //when
    Modem modem2 = modemRepository.save(createModem("modem2"));
    InstallDto.InstallRequest changeModemRequestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_CHANGE.getCode())
        .comment("단말기 교체 성공")
        .build();

    installService.changeModem(modem2.getId(), savedConsumer.getId(), changeModemRequestDto, createSampleFiles("change success", 2));

    em.flush();
    em.clear();

    //then
    InstallHistoryByConsumer historyByConsumer = installService.searchHistoryByConsumer(savedConsumer.getId(), PageRequest.of(0, 10));

    String currentState = historyByConsumer.getCurrentState();
    Page<historyInfo> historys = historyByConsumer.getHistorys();
    for (historyInfo history : historys) {
      System.out.println("history = " + history);
    }
  }

  @Test
  void 단말기_유지보수를_성공한다() {
    //given
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
    Modem modem = modemRepository.save(createModem("modem1"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));
    InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_INSTALLED.getCode())
        .comment("신규설치 완료")
        .build();

    installService.installModem(modem.getId(), consumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    InstallDto.InstallRequest maintenceRequestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_MAINTANCE.getCode())
        .comment("유지보수 성공")
        .build();

    //when
    installService.maintenanceModem(modem.getId(), maintenceRequestDto, createSampleFiles("maintence success", 2));

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.currentInstalledInfo(modem.getId()).orElseThrow();

    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer.getConsumerNo());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getComment()).isEqualTo(maintenceRequestDto.getComment());
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(maintenceRequestDto.getWorkTypeCd());
  }

  @Test
  void 단말기_철거를_성공한다() {
    //given
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
    Modem modem = modemRepository.save(createModem("modem"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));
    InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_INSTALLED.getCode())
        .comment("신규설치 완료")
        .build();

    installService.installModem(modem.getId(), consumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    InstallDto.InstallRequest demolishRequestDto = InstallRequest.builder()
        .workTypeCd(MODEM_INSTALL_STATUS_DEMOLISH.getCode())
        .comment("단말기 철거 성공")
        .build();

    //when

    installService.demolishModem(modem.getId(), demolishRequestDto, createSampleFiles("demolish success", 2));

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.currentInstalledInfo(modem.getId())
        .orElseThrow();

    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer.getConsumerNo());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getComment()).isEqualTo(demolishRequestDto.getComment());
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(demolishRequestDto.getWorkTypeCd());
  }

  @Test
  void 단말기_기준으로_설치내역_조회에_성공한다() {
    //given
    Modem modem = modemRepository.save(createModem("modem"));
    Consumer consumer1 = consumerRepository.save(createConsumer("consumer1"));
    Consumer consumer2 = consumerRepository.save(createConsumer("consumer2"));

    em.flush();
    em.clear();

    //when
    installRepository.save(createInstallInfo(modem, consumer1, MODEM_INSTALL_STATUS_INSTALLED, "신규설치 했음", LocalDateTime.now().minusDays(5L)));
    installRepository.save(createInstallInfo(modem, consumer1, MODEM_INSTALL_STATUS_MAINTANCE, "유지보수 했음", LocalDateTime.now().minusDays(4L)));
    installRepository.save(createInstallInfo(modem, consumer1, MODEM_INSTALL_STATUS_DEMOLISH, "철거 했음", LocalDateTime.now().minusDays(3L)));
    installRepository.save(createInstallInfo(modem, consumer2, MODEM_INSTALL_STATUS_INSTALLED, "다른 수용가에 신규설치", LocalDateTime.now().minusDays(2L)));
    installRepository.save(createInstallInfo(modem, consumer2, MODEM_INSTALL_STATUS_DEMOLISH, "철거", LocalDateTime.now().minusDays(1)));

    em.flush();
    em.clear();

    InstallHistoryByModem installHistoryByModem = installService.searchHistoryByModem(modem.getId(), PageRequest.of(0, 10));

    //TODO : 검증로직 개선 필요
    String currentState = installHistoryByModem.getCurrentState();
    System.out.println("currentState = " + currentState);
    Page<historyInfo> historys = installHistoryByModem.getHistorys();
    for (historyInfo history : historys) {
      System.out.println("history = " + history);
    }
  }

  @Test
  void 고객_기준으로_설치내역_조회에_성공한다() {
    //given
    Consumer consumer = consumerRepository.save(createConsumer("consumer"));
    Modem modem1 = modemRepository.save(createModem("modem1"));
    Modem modem2 = modemRepository.save(createModem("modem2"));

    // modem1 신규설치
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_INSTALLED, "신규설치 했음", LocalDateTime.now().minusDays(3L)));
    // modem1 유지보수
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_MAINTANCE, "유지보수 했음", LocalDateTime.now().minusDays(2L)));
    // modem1 철거
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_DEMOLISH, "철거 했음", LocalDateTime.now().minusDays(1L)));
    // modem2 신규설치
    installRepository.save(createInstallInfo(modem2, consumer, MODEM_INSTALL_STATUS_INSTALLED, "다른 단말기로 신규 설치", LocalDateTime.now()));

    em.flush();
    em.clear();

    //when
    InstallHistoryByConsumer installHistoryByConsumer = installService.searchHistoryByConsumer(consumer.getId(), PageRequest.of(0, 10));

    //then
    System.out.println("installHistoryByConsumer.getCurrentState() = " + installHistoryByConsumer.getCurrentState());
    Page<historyInfo> historys = installHistoryByConsumer.getHistorys();
    for (historyInfo history : historys) {
      System.out.println("history = " + history);
    }
  }

  @Test
  void 가장_최근_작업된_단말기상태를_조회한다() {
    //given
    Modem modem1 = modemRepository.save(createModem("modem1"));
    Modem modem2 = modemRepository.save(createModem("modem2"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));

    em.flush();
    em.clear();

    //when
    // modem1 신규설치
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_INSTALLED, "신규설치 했음", LocalDateTime.now().minusDays(3L)));
    // modem1 유지보수
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_MAINTANCE, "유지보수 했음", LocalDateTime.now().minusDays(2L)));
    // modem1 철거
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_DEMOLISH, "철거 했음", LocalDateTime.now().minusDays(1L)));
    // modem2 신규설치
    installRepository.save(createInstallInfo(modem2, consumer, MODEM_INSTALL_STATUS_INSTALLED, "다른 단말기로 신규 설치", LocalDateTime.now()));

    em.flush();
    em.clear();

    //then
    assertThat(installRepository.isInstalledModem(modem1.getId())).isFalse();
    assertThat(installRepository.isInstalledModem(modem2.getId())).isTrue();
  }
}