package com.install.domain.modem.service;

import static com.install.domain.code.entity.CodeSet.*;
import static com.install.global.exception.CustomErrorCode.*;
import static java.lang.String.valueOf;
import static org.apache.poi.ss.usermodel.DateUtil.*;
import static org.springframework.http.HttpStatus.*;

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
import com.install.domain.consumer.dto.ConsumerDto;
import com.install.domain.modem.dto.ModemDto.ModemRequest;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.exception.CustomExcelException;
import com.install.global.websocket.dto.MessageDto;
import com.install.global.websocket.handler.ProgressWebSocketHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 12.06.24
 */
@Slf4j
@Getter
@Transactional
@Service
public class ModemExcelService {

	private final ModemRepository modemRepository;
	private final ProgressWebSocketHandler progressWebSocketHandler;
	private final ConcurrentHashMap<String, List<CustomExcelException>> excelExceptionMap;
	private final int maxBufferSize;

	public ModemExcelService(
		ModemRepository modemRepository, ProgressWebSocketHandler progressWebSocketHandler, MaxBufferSizeProperties properties
	) {
		this.modemRepository = modemRepository;
		this.progressWebSocketHandler = progressWebSocketHandler;
		this.excelExceptionMap = new ConcurrentHashMap<>();
		this.maxBufferSize = properties.getMaxBufferSize();
	}

	public void uploadModemExcel(MultipartFile file, String sessionId) {
		List<ModemRequest> targetModems = readExcelFile(file, sessionId);
		for (int i = 0; i < targetModems.size(); i += maxBufferSize) {
			int end = Math.min(i + maxBufferSize, targetModems.size());
			List<ModemRequest> batchList = targetModems.subList(i, end);
			modemRepository.bulkInsert(batchList);
		}
	}

	public List<ModemRequest> readExcelFile(MultipartFile file, String sessionId) {
		List<ModemRequest> requests = new ArrayList<>();
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

					requests.add(ModemRequest.builder()
						.modemNo(validateModemNo(extractedData(sheet.getRow(i).getCell(0)), i, 0))
						.imei(validateImei(extractedData(sheet.getRow(i).getCell(1)), i, 1))
						.buildCompany(extractedData(sheet.getRow(i).getCell(2)))
						.modemTypeCd(MODEM_TYPE_NBIOT.getCode())
						.modemStatusCd(MODEM_STAUTS_NORMAL.getCode())
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

	private String validateModemNo(String modemNo, int row, int col) {
		if (modemRepository.existsByModemNo(modemNo)) {
			throw new CustomExcelException(BAD_MODEM_NUMBE, modemNo, row, col);
		}

		return modemNo;
	}

	private String validateImei(String imei, int row, int col) {
		if (modemRepository.existsByImei(imei)) {
			throw new CustomExcelException(BAD_IMEI_NUMBE, imei, row, col);
		}

		return imei;
	}
}
