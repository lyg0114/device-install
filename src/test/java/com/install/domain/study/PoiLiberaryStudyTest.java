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
	public void testCreateAndReadExcel() throws IOException {
		// Excel 파일 생성 및 데이터 추가
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Sheet1");

			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("Hello, POI!");

			try (FileOutputStream fos = new FileOutputStream(file)) {
				workbook.write(fos);
			}
		}

		// Excel 파일 읽기 및 데이터 검증
		try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
			Sheet sheet = workbook.getSheetAt(0);
			Row row = sheet.getRow(0);
			Cell cell = row.getCell(0);

			String cellValue = cell.getStringCellValue();
			assertThat(cellValue).isEqualTo("Hello, POI!");
		}
		assertThat(file.exists()).isTrue();
	}
}
