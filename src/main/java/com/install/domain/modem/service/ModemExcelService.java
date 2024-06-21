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

import com.install.domain.modem.dto.ModemDto.ModemRequest;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.exception.CustomExcelException;
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

	public ModemExcelService(ModemRepository modemRepository, ProgressWebSocketHandler progressWebSocketHandler) {
		this.modemRepository = modemRepository;
		this.progressWebSocketHandler = progressWebSocketHandler;
		this.excelExceptionMap = new ConcurrentHashMap<>();
	}

	public void uploadModemExcel(MultipartFile file, String sessionId) {
		try {
			modemRepository.bulkInsertModem(readExcelFile(file, sessionId));
			progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(100));
			progressWebSocketHandler.closeSession(sessionId);
		} catch (IOException ex) {
			log.error("[IOException] errorCode: {} | errorMessage: {} | cause: {} ", BAD_REQUEST, "소켓 연결에 실패하였습니다.", ex.getCause());
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

			for (int i = 0; i < totalRows; i++) {
				try {
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

					int progress = (i + 1) * 99 / totalRows;
					progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(progress));
				} catch (CustomExcelException ex) {
					excelExceptionMap.get(sessionId).add(ex);
					log.error("[CustomExcelException] errorCode: {} | errorMessage: {} ", ex.getErrorCode(), ex.getErrorMessage());
					progressWebSocketHandler.sendProgressUpdate(sessionId, ex.getTargetInfo());
				}
			}

		} catch (IOException ex) {
			log.error("[CustomExcelException] errorCode: {} | errorMessage: {} ", BAD_REQUEST, "엑셀파일 읽기를 실패했습니다.");
		}

		return requests;
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
