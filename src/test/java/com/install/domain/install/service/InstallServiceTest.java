package com.install.domain.install.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto;
import com.install.domain.install.dto.InstallDto.InstallHistoryByModem;
import com.install.domain.install.dto.InstallDto.InstallHistoryByModem.historyInfo;
import com.install.domain.install.dto.InstallDto.InstallRequest;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.security.service.JwtService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

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
    codeRepository.save(Code.builder().code("cd01").name("type-1").level(1).build());
    codeRepository.save(Code.builder().code("cd02").name("status-1").level(1).build());
    codeRepository.save(Code.builder().code("cd03").name("작업종류").level(1).build());
    codeRepository.save(Code.builder().code("cd0301").name("신규설치").level(2).build());
    codeRepository.save(Code.builder().code("cd0302").name("유지보수").level(2).build());
    codeRepository.save(Code.builder().code("cd0303").name("철거").level(2).build());
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
        .modemTypeCd(Code.builder().code("cd01").build())
        .modemStatusCd(Code.builder().code("cd02").build())
        .build();
  }

  private InstallInfo createInstallInfo(Modem modem, Consumer consumer, String workTypeCode,
      String comment, LocalDateTime workTime) {
    return InstallInfo.builder()
        .modem(Modem.builder().id(modem.getId()).build())
        .consumer(Consumer.builder().id(consumer.getId()).build())
        .workTypeCd(Code.builder().code(workTypeCode).build())
        .comment(comment)
        .workTime(workTime)
        .build();
  }

  @Test
  void 단말기_설치를_성공한다() {
    //given
    Member worker = Member.builder()
        .name("작업자")
        .nickname("에이스")
        .email("worker@example.com")
        .password("1234")
        .build();

    Member savedWorker = memberRepository.save(worker);
    when(jwtService.getId()).thenReturn(savedWorker.getId());
    Modem savedModem = modemRepository.save(createModem("test"));
    Consumer savedConsumer = consumerRepository.save(createConsumer("test"));
    InstallDto.InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd("cd0301")
        .comment("install success")
        .build();

    em.flush();
    em.clear();

    //when
    installService.installModem(savedModem.getId(), savedConsumer.getId(), requestDto);

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.findAll().get(0);
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(requestDto.getWorkTypeCd());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(savedModem.getModemNo());
    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(savedConsumer.getConsumerNo());
  }

  @Test
  void 단말기_유지보수를_성공한다() {
    //given
    Member worker = Member.builder()
        .name("작업자")
        .nickname("에이스")
        .email("worker@example.com")
        .password("1234")
        .build();

    Member savedWorker = memberRepository.save(worker);
    when(jwtService.getId()).thenReturn(savedWorker.getId());
    Modem modem = modemRepository.save(createModem("modem1"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));
    installService.installModem(modem.getId(), consumer.getId(),
        InstallRequest.builder()
            .workTypeCd("cd0301")
            .comment("신규설치 완료")
            .build());

    em.flush();
    em.clear();

    InstallDto.InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd("cd0302")
        .comment("유지보수 성공")
        .build();

    //when
    installService.maintenanceModem(modem.getId(), requestDto);

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.currentInstalledInfo(modem.getId())
        .orElseThrow();

    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer.getConsumerNo());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getComment()).isEqualTo(requestDto.getComment());
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(requestDto.getWorkTypeCd());
  }

  @Test
  void 단말기_철거를_성공한다() {
    //given
    Member worker = Member.builder()
        .name("작업자")
        .nickname("에이스")
        .email("worker@example.com")
        .password("1234")
        .build();

    Member savedWorker = memberRepository.save(worker);
    when(jwtService.getId()).thenReturn(savedWorker.getId());
    Modem modem = modemRepository.save(createModem("modem1"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));
    installService.installModem(modem.getId(), consumer.getId(),
        InstallRequest.builder()
            .workTypeCd("cd0301")
            .comment("신규설치 완료")
            .build());

    em.flush();
    em.clear();

    InstallDto.InstallRequest requestDto = InstallRequest.builder()
        .workTypeCd("cd0303")
        .comment("단말기 철거 성공")
        .build();

    //when
    installService.demolishModem(modem.getId(), requestDto);

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.currentInstalledInfo(modem.getId())
        .orElseThrow();

    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer.getConsumerNo());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getComment()).isEqualTo(requestDto.getComment());
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(requestDto.getWorkTypeCd());
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
    installRepository.save(createInstallInfo(modem, consumer1, "cd0301", "신규설치 했음", LocalDateTime.now().minusDays(5L)));
    installRepository.save(createInstallInfo(modem, consumer1, "cd0302", "유지보수 했음", LocalDateTime.now().minusDays(4L)));
    installRepository.save(createInstallInfo(modem, consumer1, "cd0303", "철거 했음", LocalDateTime.now().minusDays(3L)));
    installRepository.save(createInstallInfo(modem, consumer2, "cd0301", "다른 수용가에 신규설치", LocalDateTime.now().minusDays(2L)));
    installRepository.save(createInstallInfo(modem, consumer2, "cd0303", "철거", LocalDateTime.now().minusDays(1)));

    em.flush();
    em.clear();

    InstallHistoryByModem installHistoryByModem = installService.searchHistoryByModem(modem.getId(),
        PageRequest.of(0, 10));

    //TODO : 검증로직 개선 필요
    String currentState = installHistoryByModem.getCurrentState();
    System.out.println("currentState = " + currentState);
    Page<historyInfo> historys = installHistoryByModem.getHistorys();
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
    installRepository.save(
        InstallInfo.builder()
            .modem(Modem.builder().id(modem1.getId()).build())
            .consumer(Consumer.builder().id(consumer.getId()).build())
            .workTypeCd(Code.builder().code("cd0301").build())
            .comment("신규설치 했음")
            .workTime(LocalDateTime.now().minusDays(3L))
            .build());

    // modem1 유지보수
    installRepository.save(
        InstallInfo.builder()
            .modem(Modem.builder().id(modem1.getId()).build())
            .consumer(Consumer.builder().id(consumer.getId()).build())
            .workTypeCd(Code.builder().code("cd0302").build())
            .comment("유지보수 했음")
            .workTime(LocalDateTime.now().minusDays(2L))
            .build());

    // modem1 철거
    installRepository.save(
        InstallInfo.builder()
            .modem(Modem.builder().id(modem1.getId()).build())
            .consumer(Consumer.builder().id(consumer.getId()).build())
            .workTypeCd(Code.builder().code("cd0303").build())
            .comment("철거 했음")
            .workTime(LocalDateTime.now().minusDays(1L))
            .build());

    // modem2 신규설치
    installRepository.save(
        InstallInfo.builder()
            .modem(Modem.builder().id(modem2.getId()).build())
            .consumer(Consumer.builder().id(consumer.getId()).build())
            .workTypeCd(Code.builder().code("cd0301").build())
            .comment("다른 단말기로 신규 설치")
            .workTime(LocalDateTime.now())
            .build());

    em.flush();
    em.clear();

    //then
    assertThat(installRepository.isInstalledModem(modem1.getId())).isFalse();
    assertThat(installRepository.isInstalledModem(modem2.getId())).isTrue();
  }
}