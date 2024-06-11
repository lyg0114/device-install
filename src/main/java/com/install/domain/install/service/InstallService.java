package com.install.domain.install.service;

import static com.install.domain.code.entity.CodeSet.HAS_MODEM;
import static com.install.domain.code.entity.CodeSet.HAS_NOT_MODEM;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_DEMOLISH;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_INSTALLED;
import static com.install.domain.code.entity.CodeSet.MODEM_INSTALL_STATUS_MAINTANCE;
import static com.install.global.exception.CustomErrorCode.ALREADY_INSTALLED_MODEM;
import static com.install.global.exception.CustomErrorCode.CONSUMER_NOT_EXIST;
import static com.install.global.exception.CustomErrorCode.IMAGE_SIZE_NOT_ALLOW;
import static com.install.global.exception.CustomErrorCode.INVALID_VALUE;
import static com.install.global.exception.CustomErrorCode.IN_CORRECT_WORKTIME;
import static com.install.global.exception.CustomErrorCode.MODEM_NOT_EXIST;
import static com.install.global.exception.CustomErrorCode.NOT_FOUND_FILE_INFO;
import static com.install.global.exception.CustomErrorCode.NOT_FOUND_INSTALL_INFO;
import static com.install.global.exception.CustomErrorCode.NOT_INSTALLED_MODEM;
import static com.install.global.exception.CustomErrorCode.USER_NOT_FOUND;
import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;

import com.install.domain.code.entity.Code;
import com.install.domain.code.entity.CodeSet;
import com.install.domain.common.file.entity.FileInfo;
import com.install.domain.common.file.entity.repository.FileInfoRepository;
import com.install.domain.common.file.service.StorageService;
import com.install.domain.consumer.entity.Consumer;
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
import com.install.global.exception.CustomException;
import com.install.global.security.service.JwtService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.service
 * @since : 05.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class InstallService {

  private final MemberRepository memberRepository;

  private final InstallRepository installRepository;
  private final ModemRepository modemRepository;
  private final ConsumerRepository consumerRepository;
  private final JwtService jwtService;
  private final StorageService storageService;
  private final FileInfoRepository fileInfoRepository;

  private void saveWorkHistory(
      Long modemId, Long consumerId, InstallRequest requestDto, CodeSet codeSet, List<MultipartFile> installImages
  ) {
    Member worker = memberRepository.findById(jwtService.getId())
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    InstallInfo savedInstallInfo = installRepository.save(
        InstallInfo.builder()
            .modem(Modem.builder().id(modemId).build())
            .consumer(Consumer.builder().id(consumerId).build())
            .comment(requestDto.getComment())
            .worker(worker)
            .workTypeCd(Code.builder()
                .code(codeSet.getCode())
                .build())
            .workTime(!isNull(requestDto.getWorkTime()) ? requestDto.getWorkTime() : LocalDateTime.now())
            .build());

    installImages.forEach(image -> {
      workSpaceImagesUpload(savedInstallInfo, image);
    });
  }

  private void workSpaceImagesUpload(InstallInfo installInfo, MultipartFile multipartFile) {
    // 파일경로 DB 저장
    StringBuilder builder = new StringBuilder();
    String fileType = "." + multipartFile.getContentType().split("/")[1];
    String randomNum = randomUUID().toString().substring(0, 6);

    String fileName = builder
        .append(System.currentTimeMillis()).append("_").append(randomNum).append(fileType)
        .toString();

    builder.delete(0, builder.length()); // builder 초기화

    String dirPath = builder
        .append("modeminstall").append("/").append(installInfo.getId()).append("/")
        .toString();

    FileInfo savedFileInfo = fileInfoRepository.save(FileInfo.builder()
        .fileUri(dirPath + "/" + fileName)
        .fileSize(multipartFile.getSize())
        .installInfo(installInfo)
        .build());

    // 저장소에 실제 파일 저장
    storageService.store(multipartFile, dirPath, fileName);
  }

  /**
   * @param modemId
   * @param consumerId
   * @param requestDto
   *
   * 단말기 설치
   */
  public void installModem(
      Long modemId, Long consumerId, InstallDto.InstallRequest requestDto, List<MultipartFile> installImages
  ) {

    validateIsExistFiles(installImages);
    validateIsExistModem(modemId);
    validateIsExistConsumer(consumerId);
    validateShouldNotInstalledModem(modemId);
    validateWorkTime(modemId, requestDto);

    Modem modem = modemRepository.findById(modemId)
        .orElseThrow(() -> new CustomException(MODEM_NOT_EXIST));

    consumerRepository.findById(consumerId)
        .orElseThrow(() -> new CustomException(CONSUMER_NOT_EXIST))
        .installedModem(modem, requestDto);

    saveWorkHistory(modemId, consumerId, requestDto, MODEM_INSTALL_STATUS_INSTALLED, installImages);
  }

  public void installModem(Modem modem, Consumer consumer, List<MultipartFile> installImages, LocalDateTime workTime ) {
    InstallRequest installSuccess = InstallRequest.builder()
        .workTime(workTime)
        .comment("설치 성공").build();
    installModem(modem.getId(), consumer.getId(), installSuccess, installImages);
  }

  /**
   * @param modemId
   * @param requestDto
   *
   * 단말기 유지보수 : 철거하지않고 수리만 하는 경우
   */
  // TODO : testcase - 최초설치인데, 유지보수 api를 call한 경우
  public void maintenanceModem(Long modemId, InstallRequest requestDto, List<MultipartFile> maintenanceImages) {
    validateIsExistModem(modemId);
    validateShouldInstalledModem(modemId);
    validateWorkTime(modemId, requestDto);

    Long installedConsumerSid = getLatestStateInfo(modemId).getConsumer().getId();
    saveWorkHistory(modemId, installedConsumerSid, requestDto, MODEM_INSTALL_STATUS_MAINTANCE, maintenanceImages);
  }

  public void maintenanceModem(Modem modem, List<MultipartFile> maintenanceImages, LocalDateTime workTime) {
    InstallRequest maintenanceSuccess = InstallRequest.builder()
        .workTime(workTime)
        .comment("유지보수 성공").build();
    maintenanceModem(modem.getId(), maintenanceSuccess, maintenanceImages);
  }

  /**
   * @param modemId
   * @param requestDto
   *
   * 단말기 철거
   */
  // TODO : testcase - 최초설치인데, 철거 api를 call한 경우
  public void demolishModem(Long modemId, InstallRequest requestDto, List<MultipartFile> demolishImages) {
    validateIsExistModem(modemId);
    validateShouldInstalledModem(modemId);
    validateWorkTime(modemId, requestDto);

    Long installedConsumerSid = getLatestStateInfo(modemId).getConsumer().getId();

    consumerRepository.findById(installedConsumerSid)
        .orElseThrow(() -> new CustomException(CONSUMER_NOT_EXIST))
        .demolishModem();

    saveWorkHistory(modemId, installedConsumerSid, requestDto, MODEM_INSTALL_STATUS_DEMOLISH, demolishImages);
  }

  public void demolishModem(Modem modem, List<MultipartFile> demolishImages, LocalDateTime workTime) {
    InstallRequest demolishSuccess = InstallRequest.builder()
        .workTime(workTime)
        .comment("철거 성공").build();
    demolishModem(modem.getId(), demolishSuccess, demolishImages);
  }

  @Transactional(readOnly = true)
  public InstallHistoryByModem searchHistoryByModem(Long modemId, Pageable pageable) {
    Page<historyInfo> historyInfos = installRepository.searchInstallInfoPageByModem(modemId,
            pageable)
        .map(getInstallInfohistoryInfoFunction());

    return InstallHistoryByModem.builder()
        .historys(historyInfos)
        .currentState(checkState(historyInfos.getContent()))
        .build();
  }

  @Transactional(readOnly = true)
  public InstallHistoryByConsumer searchHistoryByConsumer(Long consumerId, Pageable pageable) {
    Page<historyInfo> historyInfos = installRepository.searchInstallInfoPageByConsumer(consumerId, pageable)
        .map(getInstallInfohistoryInfoFunction());

    return InstallHistoryByConsumer.builder()
        .historys(historyInfos)
        .currentState(checkState(historyInfos.getContent()))
        .build();
  }

  private Function<InstallInfo, historyInfo> getInstallInfohistoryInfoFunction() {
    return installInfo -> historyInfo.builder()
        .workTime(installInfo.getWorkTime())
        .workType(installInfo.getWorkTypeCd().getCode())
        .modemNo(installInfo.getModem().getModemNo())
        .consumerNo(installInfo.getConsumer().getConsumerNo())
        .consumerName(installInfo.getConsumer().getConsumerName())
        .meterNo(installInfo.getConsumer().getMeterNo())
        .city(installInfo.getConsumer().getAddress() != null ? installInfo.getConsumer().getAddress().getCity() : null)
        .build();
  }

  private String checkState(List<historyInfo> historyInfos) {
    if (historyInfos == null || historyInfos.size() < 1) {
      return HAS_NOT_MODEM.getCode();
    }

    historyInfo historyInfo = historyInfos.get(0);
    if (historyInfo.getWorkType().equals(MODEM_INSTALL_STATUS_DEMOLISH.getCode())) {
      return HAS_NOT_MODEM.getCode();
    }

    return HAS_MODEM.getCode();
  }

  /**
   * 설치 사진이 포함 되어 있는지 체크
   */
  private void validateIsExistFiles(List<MultipartFile> images) {
    if (isNull(images) || images.size() == 0) {
      throw new CustomException(NOT_FOUND_FILE_INFO);
    }
  }

  /**
   * 이미지 저장 갯수 체크
   */
  private void validateMaxImages(List<MultipartFile> images) {
    if (images.size() > 3) {
      throw new CustomException(IMAGE_SIZE_NOT_ALLOW);
    }
  }

  private InstallInfo getLatestStateInfo(Long modemId) {
    InstallInfo stateInfo = installRepository.latestStateInfo(modemId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_INSTALL_INFO));
    return stateInfo;
  }

  /**
   * 유효한 작업 시간인지 체크
   */
  private void validateWorkTime(Long modemId, InstallRequest requestDto) {
    InstallInfo latestInfo;
    try {
      latestInfo = getLatestStateInfo(modemId);
    } catch (CustomException ex) {
      log.info("[CustomException] errorCode: {} | errorMessage: {} ", ex.getErrorCode(), ex.getMessage());
      return;
    }

    if (isNull(latestInfo) || isNull(requestDto.getWorkTime())
        || latestInfo.getWorkTime().isAfter(requestDto.getWorkTime())
    ) {
      throw new CustomException(IN_CORRECT_WORKTIME);
    }
  }

  /**
   * 시스템에 등록된 모뎀인지 체크
   */
  private void validateIsExistModem(Long modemId) {
    if (!modemRepository.existsById(modemId)) {
      throw new CustomException(MODEM_NOT_EXIST);
    }
  }

  /**
   * 시스템에 등록된 고객정보인지 체크
   */
  private void validateIsExistConsumer(Long consumerId) {
    if (!consumerRepository.existsById(consumerId)) {
      throw new CustomException(CONSUMER_NOT_EXIST);
    }
  }

  /**
   * 모뎀은 설치되어 있어있지 않아야 한다.
   */
  private void validateShouldNotInstalledModem(Long modemId) {
    if (installRepository.isInstalledModem(modemId)) {
      throw new CustomException(ALREADY_INSTALLED_MODEM);
    }
  }

  /**
   * 현재 설치되어있는 모뎀인지 체크
   */
  private void validateShouldInstalledModem(Long modemId) {
    Modem modem = modemRepository.findById(modemId)
        .orElseThrow(() -> new CustomException(MODEM_NOT_EXIST));
    if (!installRepository.isInstalledModem(modemId) && isNull(modem.getInstalledConsumer())) {
      throw new CustomException(NOT_INSTALLED_MODEM);
    }
  }
}
