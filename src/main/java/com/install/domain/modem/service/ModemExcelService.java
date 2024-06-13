package com.install.domain.modem.service;

import static com.install.domain.code.entity.CodeSet.*;
import static java.lang.String.valueOf;
import static org.apache.poi.ss.usermodel.DateUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
		List<ModemRequest> requests = readExcelFile(file);
		int totalRows = requests.size();

		try {
			for (int i = 0; i < totalRows; i++) {
				// upload logic
				int progress = (i + 1) * 100 / totalRows;
				progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(progress));
			}

			progressWebSocketHandler.closeSession(sessionId);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<ModemRequest> readExcelFile(MultipartFile file) {
		List<ModemRequest> requests = new ArrayList<>();
		try (InputStream is = file.getInputStream(); Workbook workbook = new HSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);

			boolean firstRow = true;

			for (Row row : sheet) {
				// 첫 번째 행 skip
				if (firstRow) {
					firstRow = false;
					continue;
				}

				requests.add(ModemRequest.builder()
					.modemNo(extractedData(row.getCell(0)))
					.imei(extractedData(row.getCell(1)))
					.buildCompany(extractedData(row.getCell(2)))
					.modemTypeCd(MODEM_TYPE_NBIOT.getCode())
					.modemStatusCd(MODEM_STAUTS_NORMAL.getCode())
					.build());
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
			case NUMERIC -> value = isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : valueOf(cell.getNumericCellValue());
			case BOOLEAN -> value = valueOf(cell.getBooleanCellValue());
			case FORMULA -> value = cell.getCellFormula();
			default -> value = "";
		}
		return value;
	}

}
