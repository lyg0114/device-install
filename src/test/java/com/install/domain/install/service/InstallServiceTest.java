package com.install.domain.install.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.dto.InstallDto;
import com.install.domain.install.dto.InstallDto.InstallRequest;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
  private InstallService installService;

  @Autowired
  private InstallRepository installRepository;

  @Autowired
  private ModemRepository modemRepository;

  @Autowired
  private ConsumerRepository consumerRepository;

  @Autowired
  private CodeRepository codeRepository;

  @Autowired
  EntityManager em;

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

  @Test
  void 단말기_설치를_성공한다() {
    //given
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
    InstallInfo demolishModem = installRepository.isInstalledModem(modem1.getId());
    assertThat(demolishModem).isNull();

    InstallInfo installedModem = installRepository.isInstalledModem(modem2.getId());
    assertThat(installedModem).isNotNull();
  }
}