package com.install.domain.modem.service;

import static com.install.domain.code.entity.CodeSet.*;
import static java.lang.String.valueOf;
import static org.apache.poi.ss.usermodel.DateUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.modem.dto.ModemDto.ModemRequest;
import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.websocket.handler.ProgressWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 12.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ModemExcelService {

	private final ModemRepository modemRepository;
	private final ProgressWebSocketHandler progressWebSocketHandler;

	public void uploadModemExcel(MultipartFile file, String sessionId) {
		try {
			modemRepository.bulkInsertModem(readExcelFile(file, sessionId));
			progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(100));
			progressWebSocketHandler.closeSession(sessionId);
		} catch (IOException e) {
			// TODO : 적절한 예외처리 로직 필요
			throw new RuntimeException(e);
		}
	}

	public List<ModemRequest> readExcelFile(MultipartFile file, String sessionId) {
		List<ModemRequest> requests = new ArrayList<>();
		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			int totalRows = sheet.getLastRowNum() + 1;

			boolean firstRow = true;

			for (int i = 0; i < totalRows; i++) {
				if (firstRow) {
					firstRow = false;
					continue;
				}

				requests.add(ModemRequest.builder()
					.modemNo(extractedData(sheet.getRow(i).getCell(0)))
					.imei(extractedData(sheet.getRow(i).getCell(1)))
					.buildCompany(extractedData(sheet.getRow(i).getCell(2)))
					.modemTypeCd(MODEM_TYPE_NBIOT.getCode())
					.modemStatusCd(MODEM_STAUTS_NORMAL.getCode())
					.build());

				int progress = (i + 1) * 99 / totalRows;
				progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(progress));

			}
		} catch (IOException e) {
			// TODO : 적절한 예외처리 로직 필요
			throw new RuntimeException(e);
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
}
