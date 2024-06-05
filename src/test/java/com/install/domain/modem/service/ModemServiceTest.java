package com.install.domain.modem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.modem.dto.ModemDto;
import com.install.domain.modem.dto.ModemDto.ModemRequest;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import jakarta.persistence.EntityManager;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 05.06.24
 */
@Transactional
@SpringBootTest
class ModemServiceTest {

  @Autowired
  private ModemService modemService;

  @Autowired
  private ModemRepository modemRepository;

  @Autowired
  private CodeRepository codeRepository;

  @Autowired
  EntityManager em;

  @BeforeEach
  public void before() {
    createCodes();
  }

  private void createCodes() {
    codeRepository.save(Code.builder().code("cd01").name("type-1").level(1).build());
    codeRepository.save(Code.builder().code("cd02").name("status-1").level(1).build());
  }

  private ModemRequest createModemRequest(Long id) {
    return ModemRequest.builder()
        .modemNo("modemNo-" + id)
        .imei("imei-" + id)
        .buildCompany("comapnty-" + id)
        .modemTypeCd("cd01")
        .modemStatusCd("cd02")
        .build();
  }

  private Modem createModem(Long id) {
    return Modem.builder()
        .id(id)
        .modemNo("modemNo-" + id)
        .imei("imei-" + id)
        .buildCompany("comapnty-" + id)
        .modemTypeCd(Code.builder().code("cd01").build())
        .modemStatusCd(Code.builder().code("cd02").build())
        .build();
  }

  @Test
  void 단말기_단건_등록에_성공한다() {
    //given
    ModemDto.ModemRequest requestDto = createModemRequest(1L);

    //when
    modemService.addModem(requestDto);
    em.flush();
    em.clear();

    //then
    Modem findModem = modemRepository.findByModemNo(requestDto.getModemNo()).orElseThrow();

    assertThat(findModem.getModemNo()).isEqualTo(requestDto.getModemNo());
    assertThat(findModem.getImei()).isEqualTo(requestDto.getImei());
    assertThat(findModem.getBuildCompany()).isEqualTo(requestDto.getBuildCompany());
    assertThat(findModem.getModemTypeCd().getCode()).isEqualTo(requestDto.getModemTypeCd());
    assertThat(findModem.getModemStatusCd().getCode()).isEqualTo(requestDto.getModemStatusCd());
  }

  @Test
  void 단말기_단건_수정에_성공한다() {
    //given
    Modem savedModem = modemRepository.save(createModem(1L));
    ModemRequest modemRequest = createModemRequest(2L);

    //when
    modemService.updateModem(savedModem.getId(), modemRequest);
    em.flush();
    em.clear();

    //then
    Modem findModem = modemRepository.findById(savedModem.getId()).orElseThrow();
    assertThat(findModem.getModemNo()).isEqualTo(modemRequest.getModemNo());
    assertThat(findModem.getImei()).isEqualTo(modemRequest.getImei());
    assertThat(findModem.getBuildCompany()).isEqualTo(modemRequest.getBuildCompany());
    assertThat(findModem.getModemTypeCd().getCode()).isEqualTo(modemRequest.getModemTypeCd());
    assertThat(findModem.getModemStatusCd().getCode()).isEqualTo(modemRequest.getModemStatusCd());
  }

  @Test
  void 단말기_단건_삭제에_성공한다() {
    //given
    Modem savedModem = modemRepository.save(createModem(1L));
    Long targetConsumerId = savedModem.getId();

    //when
    modemService.deleteModem(targetConsumerId);
    em.flush();
    em.clear();

    //then
    assertThrows(NoSuchElementException.class, () -> {
      modemRepository.findById(targetConsumerId)
          .orElseThrow();
    });
  }

}