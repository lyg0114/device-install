package com.install.domain.consumer.service;

import static com.install.domain.code.entity.CodeSet.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.exception.CustomExcelException;
import com.install.global.websocket.handler.ProgressWebSocketHandler;

import jakarta.persistence.EntityManager;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.service
 * @since : 28.06.24
 */
@DisplayName("고객 정보 엑셀 일괄 업로드 테스트")
@Transactional
@SpringBootTest
class ConsumerExcelServiceTest {

	@Autowired ConsumerExcelService consumerExcelService;
	@Autowired ConsumerRepository consumerRepository;
	@Autowired CodeRepository codeRepository;
	@Autowired EntityManager em;

	@MockBean ProgressWebSocketHandler progressWebSocketHandler;

	@BeforeEach
	void before() {
		createCodes();
		em.flush();
		em.clear();
	}

	@Test
	void 고객정보_엑셀_일괄업로드에_성공한다() throws IOException {
		//given
		doNothing().when(progressWebSocketHandler).sendProgressUpdate(any(), any());
		doNothing().when(progressWebSocketHandler).closeSession(any());
		String sessionId = UUID.randomUUID().toString();
		MultipartFile file = createMockImageFile();

		//when
		consumerExcelService.uploadConsumerExcel(file, sessionId);

		//then
		List<Consumer> findConsumers = consumerRepository.findAll();
		assertThat(findConsumers.size()).isEqualTo(2);
		assertThat(findConsumers.get(0).getConsumerNo()).isEqualTo("consumerNo-1");
		assertThat(findConsumers.get(0).getConsumerName()).isEqualTo("consumerName-1");
		assertThat(findConsumers.get(0).getMeterNo()).isEqualTo("meterNo-1");
		assertThat(findConsumers.get(0).getInstallDate()).isNull();
		assertThat(findConsumers.get(0).getAddress().getCity()).isEqualTo("city-1");
		assertThat(findConsumers.get(0).getAddress().getStreet()).isEqualTo("street-1");
		assertThat(findConsumers.get(0).getAddress().getZipcode()).isEqualTo("zipcode-1");
	}

	@Test
	void 고객정보_엑셀_일괄업로드_예외처리에_성공한다() throws IOException {
		//given
		doNothing().when(progressWebSocketHandler).sendProgressUpdate(any(), any());
		doNothing().when(progressWebSocketHandler).closeSession(any());

		String sessionId = UUID.randomUUID().toString();
		MultipartFile file = createMockImageFile();
		createAlreadyExistConsumer();
		em.flush();
		em.clear();

		//when
		consumerExcelService.uploadConsumerExcel(file, sessionId);

		//then
		ConcurrentHashMap<String, List<CustomExcelException>> excelExceptionMap = consumerExcelService.getExcelExceptionMap();
		List<CustomExcelException> excelExceptionList = excelExceptionMap.get(sessionId);
		assertThat(excelExceptionList.size()).isEqualTo(1);
	}

	private void createAlreadyExistConsumer() {
		consumerRepository.save(Consumer.builder()
				.consumerNo("consumerNo-1")
				.consumerName("consumerName-1")
				.meterNo("meterNo-1")
			.build());
	}

	// MockExcelFile 생성
	private MultipartFile createMockImageFile() {
		String fileName = "consumer-upload.xlsx";
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
}