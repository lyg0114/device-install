package com.install.domain.install.entity.repository;

import static com.install.domain.code.entity.CodeSet.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.CodeSet;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity.repository
 * @since : 06.06.24
 */
@Transactional
@SpringBootTest
class InstallRepositoryImplTest {

  @Autowired InstallRepository installRepository;
  @Autowired ModemRepository modemRepository;
  @Autowired ConsumerRepository consumerRepository;
  @Autowired CodeRepository codeRepository;
  @Autowired EntityManager em;

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

  /*
   [테스트 시나리오]
    - consumer1에 modem 신규설치
    - modem 유지보수
    - consumer1에서 modem 철거
    - modem을 다른 수용가 consumer2에 신규 설치
    - modem을 철거
   */
  @Test
  void searchInstallInfoPageByModem_메서드_테스트() {
    //given
    Modem modem = modemRepository.save(createModem("modem"));
    Consumer consumer1 = consumerRepository.save(createConsumer("consumer1"));
    Consumer consumer2 = consumerRepository.save(createConsumer("consumer2"));

    em.flush();
    em.clear();

    //when
    LocalDateTime now = LocalDateTime.now();
    installRepository.save(createInstallInfo(modem, consumer1, MODEM_INSTALL_STATUS_INSTALLED, "신규설치 했음", now.minusDays(5L)));
    installRepository.save(createInstallInfo(modem, consumer1, MODEM_INSTALL_STATUS_MAINTANCE, "유지보수 했음", now.minusDays(4L)));
    installRepository.save(createInstallInfo(modem, consumer1, MODEM_INSTALL_STATUS_DEMOLISH, "철거 했음", now.minusDays(3L)));
    installRepository.save(createInstallInfo(modem, consumer2, MODEM_INSTALL_STATUS_INSTALLED, "다른 수용가에 신규설치", now.minusDays(2L)));
    installRepository.save(createInstallInfo(modem, consumer2, MODEM_INSTALL_STATUS_DEMOLISH, "철거", now.minusDays(1L)));

    em.flush();
    em.clear();

    //then
    Page<InstallInfo> installInfos = installRepository.searchInstallInfoPageByModem(modem.getId(), PageRequest.of(0, 10));

    List<InstallInfo> content = installInfos.getContent();
    assertThat(content.size()).isEqualTo(5);

    InstallInfo installInfo = content.get(0);
    assertThat(installInfo.getComment()).isEqualTo("철거");
    assertThat(installInfo.getModem().getModemNo()).isEqualTo(modem.getModemNo());
    assertThat(installInfo.getConsumer().getConsumerNo()).isEqualTo(consumer2.getConsumerNo());
    assertThat(installInfo.getConsumer().getConsumerName()).isEqualTo(consumer2.getConsumerName());
    assertThat(installInfo.getWorkTypeCd().getCode()).isEqualTo(MODEM_INSTALL_STATUS_DEMOLISH.getCode());
    assertThat(installInfo.getWorkTime()).isEqualTo(now.minusDays(1L));
    assertThat(installInfo.getWorker()).isNull();
  }
}