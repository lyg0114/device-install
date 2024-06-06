package com.install.domain.install.entity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.entity.InstallInfo;
import com.install.domain.install.service.InstallService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity.repository
 * @since : 06.06.24
 */
@Transactional
@SpringBootTest
class InstallRepositoryImplTest {

  @Autowired
  InstallRepository installRepository;

  @Autowired
  ModemRepository modemRepository;

  @Autowired
  ConsumerRepository consumerRepository;

  @Autowired
  CodeRepository codeRepository;

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
    installRepository.save(createInstallInfo(modem, consumer1, "cd0301", "신규설치 했음", LocalDateTime.now().minusDays(5L)));
    installRepository.save(createInstallInfo(modem, consumer1, "cd0302", "유지보수 했음", LocalDateTime.now().minusDays(4L)));
    installRepository.save(createInstallInfo(modem, consumer1, "cd0303", "철거 했음", LocalDateTime.now().minusDays(3L)));
    installRepository.save(createInstallInfo(modem, consumer2, "cd0301", "다른 수용가에 신규설치", LocalDateTime.now().minusDays(2L)));
    installRepository.save(createInstallInfo(modem, consumer2, "cd0303", "철거", LocalDateTime.now().minusDays(1)));

    em.flush();
    em.clear();

    //then
    //TODO : 검증로직 개선 필요
    Page<InstallInfo> installInfos = installRepository.searchInstallInfoPageByModem(modem.getId(),
        PageRequest.of(0, 10));

    for (InstallInfo installInfo : installInfos) {
      System.out.println("installInfo = " + installInfo);
    }
  }
}