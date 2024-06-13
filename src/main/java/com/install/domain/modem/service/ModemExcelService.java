package com.install.domain.modem.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

	private static void sleep() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public List<List<String>> readExcelFile(MultipartFile file) throws IOException {
		List<List<String>> data = new ArrayList<>();
		try (InputStream is = file.getInputStream(); Workbook workbook = new HSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);

			for (Row row : sheet) {
				List<String> rowData = new ArrayList<>();

				for (Cell cell : row) {
					switch (cell.getCellType()) {
						case STRING -> rowData.add(cell.getStringCellValue());
						case NUMERIC -> {
							if (DateUtil.isCellDateFormatted(cell)) {
								rowData.add(cell.getDateCellValue().toString());
							} else {
								rowData.add(String.valueOf(cell.getNumericCellValue()));
							}
						}
						case BOOLEAN -> rowData.add(String.valueOf(cell.getBooleanCellValue()));
						case FORMULA -> rowData.add(cell.getCellFormula());
						default -> rowData.add("");
					}
				}

				data.add(rowData);
			}
		}

		return data;
	}

	public void uploadModemExcel(MultipartFile file, String sessionId) {

		int totalRows = getTotalRows(file);
		try {
			for (int i = 0; i < totalRows; i++) {
				int progress = (i + 1) * 100 / totalRows;
				progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(progress));
				sleep();
			}

			progressWebSocketHandler.closeSession(sessionId);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private int getTotalRows(MultipartFile file) {
		return 100;
	}
}
