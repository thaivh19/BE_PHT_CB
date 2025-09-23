package com.fis.ws.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	public static XSSFWorkbook fileToXSSFWorkbook(File excel) {
		try (FileInputStream fis = new FileInputStream(excel); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
			return workbook;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String convertWorkbookToBase64(XSSFWorkbook workbook) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			workbook.write(outputStream);
			byte[] excelBytes = outputStream.toByteArray();
			return Base64.getEncoder().encodeToString(excelBytes);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void saveWorkbookToFile(XSSFWorkbook workbook, String filePath) throws IOException {
		try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
			workbook.write(fileOut);
		}
		workbook.close();
		System.out.println("File Excel đã được lưu tại: " + filePath);
	}

	public static void createCell(XSSFSheet sheet, Row row, int columnCount, Object valueOfCell, CellStyle style) {
//		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (valueOfCell == null) {
			cell.setCellValue("");
		} else if (valueOfCell instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) valueOfCell).stripTrailingZeros().toPlainString());
		} else if (valueOfCell instanceof Integer) {
			cell.setCellValue((Integer) valueOfCell);
		} else if (valueOfCell instanceof Long) {
			cell.setCellValue((Long) valueOfCell);
		} else if (valueOfCell instanceof String) {
			cell.setCellValue(valueOfCell.toString());
		} else {
			cell.setCellValue((Boolean) valueOfCell);
		}
		cell.setCellStyle(style);
	}

	public static void createCellMoneyFomart(XSSFSheet sheet, Row row, int columnCount, Object valueOfCell,
			CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);

		if (valueOfCell == null) {
			cell.setCellValue("");
		} else if (valueOfCell instanceof BigDecimal || valueOfCell instanceof Double || valueOfCell instanceof Float) {
			double amount = valueOfCell instanceof BigDecimal ? ((BigDecimal) valueOfCell).doubleValue()
					: ((Number) valueOfCell).doubleValue();
			cell.setCellValue(amount);
			CellStyle moneyStyle = sheet.getWorkbook().createCellStyle();
			DataFormat format = sheet.getWorkbook().createDataFormat();
			moneyStyle.setDataFormat(format.getFormat("#,##0.00")); // Định dạng tiền tệ
			cell.setCellStyle(moneyStyle);
		} else if (valueOfCell instanceof Integer || valueOfCell instanceof Long) {
			cell.setCellValue(((Number) valueOfCell).doubleValue());
		} else if (valueOfCell instanceof String) {
			cell.setCellValue((String) valueOfCell);
		} else {
			cell.setCellValue((Boolean) valueOfCell);
		}
		if (!(valueOfCell instanceof BigDecimal || valueOfCell instanceof Double || valueOfCell instanceof Float)) {
			cell.setCellStyle(style);
		}
	}

	public static void convertToCsv(XSSFWorkbook workbook, Path csvPath) throws IOException {
		try (OutputStreamWriter csvWriter = new OutputStreamWriter(new FileOutputStream(csvPath.toFile()),
				StandardCharsets.UTF_8)) {
			csvWriter.write('\uFEFF');
			for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
				XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				for (Row row : sheet) {
					StringBuilder rowContent = new StringBuilder();
					for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
						Cell cell = row.getCell(cellIndex);
						String cellValue = getCellValueAsString(cell);
						rowContent.append(cellValue);
//						rowContent.append("\"").append(cellValue).append("\"");
						if (cellIndex < row.getLastCellNum() - 1) {
							rowContent.append(",");
						}
					}
					csvWriter.write(rowContent.toString());
					csvWriter.write("\n");
				}
			}
		}
	}

	public static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				// Giữ nguyên định dạng số nếu chứa 0 đầu
				DataFormatter dataFormatter = new DataFormatter();
				return dataFormatter.formatCellValue(cell);
			}
		case BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case FORMULA:
			// Xử lý công thức
			try {
				return cell.getStringCellValue(); // Nếu công thức trả về chuỗi
			} catch (IllegalStateException e) {
				return Double.toString(cell.getNumericCellValue()); // Nếu công thức trả về số
			}
		case BLANK:
			return "";
		default:
			return "";
		}
	}
}
