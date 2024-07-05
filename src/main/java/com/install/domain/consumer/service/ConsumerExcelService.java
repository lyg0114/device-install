package com.install.domain.consumer.service;

import static com.install.global.exception.CustomErrorCode.*;
import static java.lang.String.valueOf;
import static org.apache.poi.ss.usermodel.DateUtil.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.util.StringUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.common.config.MaxBufferSizeProperties;
import com.install.domain.consumer.dto.ConsumerDto.ConsumerRequest;
import com.install.domain.consumer.entity.repository.ConsumerRepository;
import com.install.global.exception.CustomExcelException;
import com.install.global.websocket.dto.MessageDto;
import com.install.global.websocket.handler.ProgressWebSocketHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 12.06.82
 */
@Slf4j
@Getter
@Transactional
@Service
public class ConsumerExcelService {

	private final ConsumerRepository consumerRepository;
	private final ProgressWebSocketHandler progressWebSocketHandler;
	private final ConcurrentHashMap<String, List<CustomExcelException>> excelExceptionMap;
	private final int maxBufferSize;

	public ConsumerExcelService(
		ConsumerRepository consumerRepository, ProgressWebSocketHandler progressWebSocketHandler, MaxBufferSizeProperties properties
	) {
		this.consumerRepository = consumerRepository;
		this.progressWebSocketHandler = progressWebSocketHandler;
		this.excelExceptionMap = new ConcurrentHashMap<>();
		this.maxBufferSize = properties.getMaxBufferSize();
	}

	public void uploadConsumerExcel(MultipartFile file, String sessionId) {
		List<ConsumerRequest> targetConsumers = readExcelFile(file, sessionId);
		// targetConsumers 리스트를 maxBufferSize 크기로 나누어 배치 삽입
		for (int i = 0; i < targetConsumers.size(); i += maxBufferSize) {
			int end = Math.min(i + maxBufferSize, targetConsumers.size());
			List<ConsumerRequest> batchList = targetConsumers.subList(i, end);
			consumerRepository.bulkInsert(batchList);
		}
	}

	public List<ConsumerRequest> readExcelFile(MultipartFile file, String sessionId) {
		List<ConsumerRequest> requests = new ArrayList<>();
		List<CustomExcelException> excelExceptionList = new ArrayList<>();
		excelExceptionMap.put(sessionId, excelExceptionList);

		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			int totalRows = sheet.getLastRowNum() + 1;

			boolean firstRow = true;
			int progress = 0;

			for (int i = 0; i < totalRows; i++) {
				// 업로드 진행률 계산
				progress = (i + 1) * 99 / totalRows;

				try {

					// 첫번째줄 skip ( 메타 정보 )
					if (firstRow) {
						firstRow = false;
						continue;
					}

					requests.add(ConsumerRequest.builder()
						.consumerNo(validateConsumerNo(extractedData(sheet.getRow(i).getCell(0)), i, 0))
						.consumerName(validateConsumerName(extractedData(sheet.getRow(i).getCell(1)), i, 1))
						.meterNo(validateMeterNo(extractedData(sheet.getRow(i).getCell(2)), i, 2))
						.city(extractedData(sheet.getRow(i).getCell(3)))
						.street(extractedData(sheet.getRow(i).getCell(4)))
						.zipcode(extractedData(sheet.getRow(i).getCell(5)))
						.geoX(extractedData(sheet.getRow(i).getCell(6)))
						.geoY(extractedData(sheet.getRow(i).getCell(7)))
						.build());

				} catch (CustomExcelException ex) {
					// 잘못된 입력으로 예외상황 발생
					// 테스트 검증을 위한 ConcurrentHashMap
					excelExceptionMap.get(sessionId).add(ex);
					log.error("[CustomExcelException] errorCode: {} | errorMessage: {} ", ex.getErrorCode(), ex.getErrorMessage());
					// 유효성 처리 데이터 전달
					progressWebSocketHandler.sendProgressUpdate(sessionId, MessageDto.builder()
						.isSuccess(false)
						.row(ex.getRow())
						.col(ex.getCol())
						.value(ex.getValue())
						.progress(progress)
						.build()
					);
				}

				progressWebSocketHandler.sendProgressUpdate(sessionId, MessageDto.builder()
					.isSuccess(true)
					.progress(progress)
					.build()
				);
			}

			// 마지막 작업 100%
			progressWebSocketHandler.sendProgressUpdate(sessionId, MessageDto.builder()
				.isSuccess(true)
				.progress(100)
				.build()
			);

		} catch (IOException ex) {
			log.error("[CustomExcelException] errorCode: {} | errorMessage: {} ", BAD_REQUEST, "엑셀파일 읽기를 실패했습니다.");
		}

		// websocket 연결 종료
		// TODO : DB Insert 에 문제가 발생했을경우를 고려해서 개선 필요
		colseWebSocketSession(sessionId);

		return requests;
	}

	private void colseWebSocketSession(String sessionId) {
		try {
			progressWebSocketHandler.closeSession(sessionId);
		} catch (IOException e) {
			log.error("[CustomExcelException] errorCode: {} | errorMessage: {} ", BAD_REQUEST, "엑셀파일 읽기를 실패했습니다.");
		}
	}

	private String extractedData(Cell cell) {
		String value;
		switch (cell.getCellType()) {
			case STRING -> value = cell.getStringCellValue();
			case NUMERIC -> value = isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : valueOf((int)cell.getNumericCellValue());
			case BOOLEAN -> value = valueOf(cell.getBooleanCellValue());
			case FORMULA -> value = cell.getCellFormula();
			default -> value = "";
		}

		return value;
	}

	private String validateConsumerNo(String consumerNo, int row, int col) {
		if (consumerRepository.existsByConsumerNo(consumerNo)) {
			throw new CustomExcelException(CONSUMER_NOT_EXIST, consumerNo, row, col);
		}
		return consumerNo;
	}

	private String validateConsumerName(String consumerNo, int row, int col) {
		if (!hasText(consumerNo)) {
			throw new CustomExcelException(CONSUMER_NAME_SHOUD_NOT_NULL, consumerNo, row, col);
		}
		return consumerNo;
	}

	private String validateMeterNo(String meterNo, int row, int col) {
		if (consumerRepository.existsByMeterNo(meterNo)) {
			throw new CustomExcelException(METER_NO_ALREADY_EXIST, meterNo, row, col);
		}
		return meterNo;
	}
}
