package com.install.domain.study;

import static com.install.domain.code.entity.CodeSet.MODEM_STAUTS;
import static com.install.domain.code.entity.CodeSet.MODEM_TYPE;
import static com.install.domain.code.entity.CodeSet.getAllCodes;
import static com.install.domain.consumer.entity.QConsumer.consumer;
import static com.install.domain.modem.entity.QModem.modem;
import static java.util.List.of;
import static org.mockito.Mockito.when;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Location;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.install.entity.repository.InstallRepository;
import com.install.domain.install.service.InstallService;
import com.install.domain.member.entity.Member;
import com.install.domain.member.entity.repository.MemberRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.security.service.JwtService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

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
  void query_test() {
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

    em.flush();
    em.clear();

    //when
    LocalDateTime now = LocalDateTime.now();
    installService.installModem(modem1, consumer1, of(createMockFile("설치 성공")), now.minusDays(6L));
    installService.demolishModem(modem1, of(createMockFile("철거 성공")), now.minusDays(5L));
    installService.installModem(modem1_1, consumer1, of(createMockFile("설치 성공")), now.minusDays(4L));
    installService.demolishModem(modem1_1, of(createMockFile("철거 성공")), now.minusDays(3L));

    installService.installModem(modem2, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
    installService.demolishModem(modem2, of(createMockFile("철거 성공")), now.minusDays(5L));
    installService.installModem(modem2_1, consumer2, of(createMockFile("설치 성공")), now.minusDays(6L));
    installService.demolishModem(modem2_1, of(createMockFile("철거 성공")), now.minusDays(5L));

    installService.installModem(modem3, consumer3, of(createMockFile("설치 성공")), now.minusDays(6L));

    em.flush();
    em.clear();

    System.out.println("########################################################");

    List<Consumer> fetch = queryFactory
        .select(consumer)
        .from(consumer)
        .leftJoin(consumer.installedModem, modem)
        .fetchJoin()
        .fetch();

    for (Consumer consumer : fetch) {
      System.out.println(consumer);
    }

    System.out.println("########################################################");

    List<Modem> fetch1 = queryFactory
        .select(modem)
        .from(modem)
        .leftJoin(modem.installedConsumer, consumer)
        .fetchJoin()
        .fetch();

    for (Modem modem4 : fetch1) {
      System.out.println("modem4 = " + modem4);
    }
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
    MockMultipartFile sameplFile = new MockMultipartFile("foo", "foo.txt",
        MediaType.TEXT_PLAIN_VALUE, content.getBytes());
    return sameplFile;
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
}