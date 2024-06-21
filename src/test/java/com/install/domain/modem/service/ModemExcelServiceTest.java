package com.install.domain.modem.service;

import static com.install.domain.code.entity.CodeSet.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.code.entity.repository.CodeRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.exception.CustomExcelException;
import com.install.global.websocket.handler.ProgressWebSocketHandler;

import jakarta.persistence.EntityManager;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 13.06.24
 */
@DisplayName("단말기 정보 엑셀 일괄 업로드 테스트")
@Transactional
@SpringBootTest
class ModemExcelServiceTest {

	@Autowired ModemExcelService modemExcelService;
	@Autowired CodeRepository codeRepository;
	@Autowired ModemRepository modemRepository;
	@Autowired EntityManager em;

	@MockBean ProgressWebSocketHandler progressWebSocketHandler;

	@BeforeEach
	void before() {
		createCodes();
		em.flush();
		em.clear();
	}

	@Test
	void 단말기_엑셀_일괄업로드에_성공한다() throws IOException {
		//given
		doNothing().when(progressWebSocketHandler).sendProgressUpdate(any(), any());
		doNothing().when(progressWebSocketHandler).closeSession(any());
		String sessionId = UUID.randomUUID().toString();
		MultipartFile file = createMockImageFile();

		//when
		modemExcelService.uploadModemExcel(file, sessionId);

		//then
		List<Modem> findModems = modemRepository.findAll();
		assertThat(findModems.size()).isEqualTo(6);
		assertThat(findModems.get(0).getModemNo()).isEqualTo("2111908");
		assertThat(findModems.get(0).getImei()).isEqualTo("imei-123");
		assertThat(findModems.get(0).getBuildCompany()).isEqualTo("삼성");
		assertThat(findModems.get(0).getModemTypeCd().getCode()).isEqualTo(MODEM_TYPE_NBIOT.getCode());
		assertThat(findModems.get(0).getModemStatusCd().getCode()).isEqualTo(MODEM_STAUTS_NORMAL.getCode());
	}

	@Test
	void 단말기_엑셀_일괄업로드_예외처리에_성공한다() throws IOException {
		//given
		doNothing().when(progressWebSocketHandler).sendProgressUpdate(any(), any());
		doNothing().when(progressWebSocketHandler).closeSession(any());

		String sessionId = UUID.randomUUID().toString();
		MultipartFile file = createMockImageFile();
		createAlreadyInsertModem();
		em.flush();
		em.clear();

		//when
		modemExcelService.uploadModemExcel(file, sessionId);

		//then
		ConcurrentHashMap<String, List<CustomExcelException>> excelExceptionMap = modemExcelService.getExcelExceptionMap();
		List<CustomExcelException> excelExceptionList = excelExceptionMap.get(sessionId);
		assertThat(excelExceptionList.size()).isEqualTo(1);
	}

	// MockExcelFile 생성
	private MultipartFile createMockImageFile() {
		String fileName = "modem-upload.xlsx";
		InputStream inputStream = getClass().getResourceAsStream("/excel/" + fileName);
		byte[] bytes;
		try {
			bytes = inputStream.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return new MockMultipartFile(
			"file",
			fileName,
			null,
			bytes
		);
	}

	private void createCodes() {
		codeRepository.saveAll(getAllCodes());
	}

	private void createAlreadyInsertModem() {
		modemRepository.save(Modem.builder()
			.modemNo("2111908")
			.imei("imei-123")
			.buildCompany("삼성")
			.modemTypeCd(MODEM_TYPE_NBIOT.getCodeEntity())
			.modemStatusCd(MODEM_STAUTS_NORMAL.getCodeEntity())
			.build());
	}
}