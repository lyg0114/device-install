package com.install.domain.study;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.study
 * @since : 13.06.24
 */
public class PoiLiberaryStudyTest {

	private static final String FILE_PATH = "src/test/resources/excel/sample.xlsx";
	private File file;

	@BeforeEach
	public void setUp() {
		file = new File(FILE_PATH);
	}

	@AfterEach
	public void tearDown() {
		if (file.exists()) {
			file.delete();
		}
	}

	@Test
	public void 샘플_엑셀_파일생성을_성공한다() throws IOException {
		// Excel 파일 생성 및 데이터 추가
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Sheet1");

			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue("modemNo");
			row.createCell(1).setCellValue("imei");
			row.createCell(2).setCellValue("build-company");

			for (int i = 0; i < 10000; i++) {
				Row newRow = sheet.createRow(i + 1);
				newRow.createCell(0).setCellValue("modemNo-" + (i + 1));
				newRow.createCell(1).setCellValue("imei-" + (i + 1));
				newRow.createCell(2).setCellValue("build-company-" + (i + 1));
			}

			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}
		}
	}

	public void 샘플_엑셀_파일조회를_성공한다() throws IOException {
		try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			Row row = sheet.getRow(0);
			Cell cell = row.getCell(0);

			String cellValue = cell.getStringCellValue();
			assertThat(cellValue).isEqualTo("modemNo");
		}

		assertThat(file.exists()).isTrue();
	}
}
