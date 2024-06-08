package com.install.domain.install.service;

import static com.install.domain.code.entity.CodeSet.HAS_MODEM;
import static com.install.domain.code.entity.CodeSet.HAS_NOT_MODEM;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_DEMOLISH;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_INSTALLED;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_MAINTANCE;
import static com.install.domain.code.entity.CodeSet.MODEM_STAUTS;
import static com.install.domain.code.entity.CodeSet.MODEM_TYPE;
import static com.install.domain.code.entity.CodeSet.getAllCodes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.CodeSet;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.common.file.entity.FileInfo;
import com.install.domain.common.file.service.StorageService;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.service
 * @since : 05.06.24
 */
@DisplayName("단말기 설치,교체,유지보수,철거 테스트")
@Transactional
@SpringBootTest
class InstallServiceTest {

  @Autowired InstallService installService;
  @Autowired InstallRepository installRepository;
  @Autowired ModemRepository modemRepository;
  @Autowired ConsumerRepository consumerRepository;
  @Autowired CodeRepository codeRepository;
  @Autowired EntityManager em;
  @Autowired MemberRepository memberRepository;
  @Autowired StorageService storageService;

  @MockBean JwtService jwtService;

  @BeforeEach void before() { createCodes(); }

  private void createCodes() {
    codeRepository.saveAll(getAllCodes());
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

  private InstallInfo createInstallInfo(
      Modem modem, Consumer consumer, CodeSet workTypeCode, String comment, LocalDateTime workTime
  ) {
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
    InstallRequest requestDto = InstallRequest.builder().comment("install success !!").build();

    em.flush();
    em.clear();

    //when
    installService.installModem(savedModem.getId(), savedConsumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.findAll().get(0);
    Long fileId = installInfo.getFileInfos().get(0).getId();
    Resource resource = storageService.loadAsResource(fileId);

    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(MODEM_INSTALL_STATUS_INSTALLED.getCode());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(savedModem.getModemNo());
    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(savedConsumer.getConsumerNo());
    assertThat(resource.exists()).isTrue();
  }

  @Test
  void 단말기_유지보수를_성공한다() {
    //given
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
    Modem modem = modemRepository.save(createModem("modem1"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));
    InstallRequest requestDto = InstallRequest.builder().comment("신규설치 완료").build();

    installService.installModem(modem.getId(), consumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    InstallRequest maintenceRequestDto = InstallRequest.builder().comment("유지보수 성공").build();

    //when
    installService.maintenanceModem(modem.getId(), maintenceRequestDto, createSampleFiles("maintence success", 2));

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.currentInstallStateInfo(modem.getId()).orElseThrow();
    Long fileId = installInfo.getFileInfos().get(0).getId();
    Resource resource = storageService.loadAsResource(fileId);

    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(MODEM_INSTALL_STATUS_MAINTANCE.getCode());
    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer.getConsumerNo());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getComment()).isEqualTo(maintenceRequestDto.getComment());
  }

  @Test
  void 단말기_철거를_성공한다() {
    //given
    when(jwtService.getId()).thenReturn(memberRepository.save(createMember("worker")).getId());
    Modem modem = modemRepository.save(createModem("modem"));
    Consumer consumer = consumerRepository.save(createConsumer("test"));
    InstallRequest requestDto = InstallRequest.builder().comment("신규설치 완료").build();

    installService.installModem(modem.getId(), consumer.getId(), requestDto, createSampleFiles("install success", 2));

    em.flush();
    em.clear();

    InstallRequest demolishRequestDto = InstallRequest.builder().comment("단말기 철거 성공").build();

    //when
    installService.demolishModem(modem.getId(), demolishRequestDto, createSampleFiles("demolish success", 2));

    em.flush();
    em.clear();

    //then
    InstallInfo installInfo = installRepository.currentInstallStateInfo(modem.getId()).orElseThrow();
    Long fileId = installInfo.getFileInfos().get(0).getId();
    Resource resource = storageService.loadAsResource(fileId);

    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(MODEM_INSTALL_STATUS_DEMOLISH.getCode());
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer.getConsumerNo());
    assertThat(resource.exists()).isTrue();
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

    assertThat(installHistoryByModem.getCurrentState()).isEqualTo(HAS_NOT_MODEM.getCode());
    List<historyInfo> historys = installHistoryByModem.getHistorys().getContent();
    assertThat(historys.get(0).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_DEMOLISH.getCode());
    assertThat(historys.get(1).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_INSTALLED.getCode());
    assertThat(historys.get(2).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_DEMOLISH.getCode());
    assertThat(historys.get(3).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_MAINTANCE.getCode());
    assertThat(historys.get(4).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_INSTALLED.getCode());
  }

  @Test
  void 고객_기준으로_설치내역_조회에_성공한다() {
    //given
    Consumer consumer = consumerRepository.save(createConsumer("consumer"));
    Modem modem1 = modemRepository.save(createModem("modem1"));
    Modem modem2 = modemRepository.save(createModem("modem2"));

    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_INSTALLED, "신규설치 했음", LocalDateTime.now().minusDays(3L)));
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_MAINTANCE, "유지보수 했음", LocalDateTime.now().minusDays(2L)));
    installRepository.save(createInstallInfo(modem1, consumer, MODEM_INSTALL_STATUS_DEMOLISH, "철거 했음", LocalDateTime.now().minusDays(1L)));
    installRepository.save(createInstallInfo(modem2, consumer, MODEM_INSTALL_STATUS_INSTALLED, "다른 단말기로 신규 설치", LocalDateTime.now()));

    em.flush();
    em.clear();

    //when
    InstallHistoryByConsumer installHistoryByConsumer = installService.searchHistoryByConsumer(consumer.getId(), PageRequest.of(0, 10));
    List<historyInfo> historyInfos = installHistoryByConsumer.getHistorys().getContent();

    //then
    assertThat(installHistoryByConsumer.getCurrentState()).isEqualTo(HAS_MODEM.getCode());
    assertThat(historyInfos.get(0).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_INSTALLED.getCode());
    assertThat(historyInfos.get(1).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_DEMOLISH.getCode());
    assertThat(historyInfos.get(2).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_MAINTANCE.getCode());
    assertThat(historyInfos.get(3).getWorkType()).isEqualTo(MODEM_INSTALL_STATUS_INSTALLED.getCode());
  }
}