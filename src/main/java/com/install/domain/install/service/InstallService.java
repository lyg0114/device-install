package com.install.domain.install.service;

import static com.install.domain.code.entity.CodeSet.*;
import static com.install.global.exception.CustomErrorCode.*;
import static java.util.Objects.*;
import static java.util.UUID.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		// 작업자 조회
		Member worker = memberRepository.findById(jwtService.getId())
			.orElseThrow(() -> new CustomException(USER_NOT_FOUND));

		// 작업 정보 저장
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

		// 작업 사진 정보 저장
		installImages.forEach(image -> {
			workSpaceImagesUpload(savedInstallInfo, image);
		});
	}

	private void workSpaceImagesUpload(InstallInfo installInfo, MultipartFile multipartFile) {
		// 파일경로 생성
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

	public void installModem(
		Long modemId, Long consumerId, InstallDto.InstallRequest requestDto, List<MultipartFile> installImages
	) {

		validateIsExistFiles(installImages);
		validateIsExistModem(modemId);
		validateIsExistConsumer(consumerId);
		validateShouldNotInstalledModem(modemId);
		validateWorkTime(modemId, requestDto);

		// 현재 설치정보 저장
		Modem modem = modemRepository.findById(modemId)
			.orElseThrow(() -> new CustomException(MODEM_NOT_EXIST));
		consumerRepository.findById(consumerId)
			.orElseThrow(() -> new CustomException(CONSUMER_NOT_EXIST))
			.installedModem(modem, requestDto);

		// 설치정보 내역 저장
		saveWorkHistory(modemId, consumerId, requestDto, MODEM_INSTALL_STATUS_INSTALLED, installImages);
	}

	public void maintenanceModem(Long modemId, InstallRequest requestDto, List<MultipartFile> maintenanceImages) {
		validateIsExistModem(modemId);
		validateShouldInstalledModem(modemId);
		validateWorkTime(modemId, requestDto);

		Long installedConsumerSid = getLatestWorkStateInfo(modemId).getConsumer().getId();
		saveWorkHistory(modemId, installedConsumerSid, requestDto, MODEM_INSTALL_STATUS_MAINTANCE, maintenanceImages);
	}

	public void demolishModem(
		Long modemId, InstallRequest requestDto, List<MultipartFile> demolishImages
	) {
		validateIsExistModem(modemId);
		validateShouldInstalledModem(modemId);
		validateWorkTime(modemId, requestDto);

		Long installedConsumerSid = getLatestWorkStateInfo(modemId).getConsumer().getId();

		consumerRepository.findById(installedConsumerSid)
			.orElseThrow(() -> new CustomException(CONSUMER_NOT_EXIST))
			.demolishModem();

		saveWorkHistory(modemId, installedConsumerSid, requestDto, MODEM_INSTALL_STATUS_DEMOLISH, demolishImages);
	}

	@Transactional(readOnly = true)
	public InstallHistoryByModem searchHistoryByModem(Long modemId, Pageable pageable) {
		Page<historyInfo> historyInfos = installRepository.searchInstallInfoPageByModem(modemId, pageable)
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
			.city(installInfo.getConsumer().getAddress() != null ? installInfo.getConsumer().getAddress().getCity() :
				null)
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
	 * 작업 사진이 포함 되어 있는지 검증
	 */
	private void validateIsExistFiles(List<MultipartFile> images) {
		if (isNull(images) || images.size() == 0) {
			throw new CustomException(NOT_FOUND_FILE_INFO);
		}
	}

	/**
	 * 최대 저장 가능 이미지 갯수 검증 : 최대 3개
	 */
	private void validateMaxImages(List<MultipartFile> images) {
		if (images.size() > 3) {
			throw new CustomException(IMAGE_SIZE_NOT_ALLOW);
		}
	}

	/**
	 * 단말기의 가장 최근의 작업정보 조회
	 */
	private InstallInfo getLatestWorkStateInfo(Long modemId) {
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
			// 작업 내역이 없는경우 메서드 종료
			latestInfo = getLatestWorkStateInfo(modemId);
		} catch (CustomException ex) {
			log.info("[CustomException] errorCode: {} | errorMessage: {} ", ex.getErrorCode(), ex.getMessage());
			return;
		}

		// 작업시간 검증
		if (isNull(latestInfo) || isNull(requestDto.getWorkTime())
			|| latestInfo.getWorkTime().isAfter(requestDto.getWorkTime())
		) {
			throw new CustomException(IN_CORRECT_WORKTIME);
		}
	}

	/**
	 * 시스템에 등록된 모뎀인지 검증
	 */
	private void validateIsExistModem(Long modemId) {
		if (!modemRepository.existsById(modemId)) {
			throw new CustomException(MODEM_NOT_EXIST);
		}
	}

	/**
	 * 시스템에 등록된 고객정보인지 검증
	 */
	private void validateIsExistConsumer(Long consumerId) {
		if (!consumerRepository.existsById(consumerId)) {
			throw new CustomException(CONSUMER_NOT_EXIST);
		}
	}

	/**
	 * 모뎀 설치정보 검증 : 단말기는 설치되어 있어있지 않아야 한다.
	 */
	private void validateShouldNotInstalledModem(Long modemId) {
		if (installRepository.isInstalledModem(modemId)) {
			throw new CustomException(ALREADY_INSTALLED_MODEM);
		}
	}

	/**
	 * 모뎀 설치정보 검증 : 단말기는 설치되어 있어야 한다.
	 */
	private void validateShouldInstalledModem(Long modemId) {
		Modem modem = modemRepository.findById(modemId)
			.orElseThrow(() -> new CustomException(MODEM_NOT_EXIST));
		if (!installRepository.isInstalledModem(modemId) && isNull(modem.getInstalledConsumer())) {
			throw new CustomException(NOT_INSTALLED_MODEM);
		}
	}
}
