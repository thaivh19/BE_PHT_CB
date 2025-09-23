package com.pht.utils;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Data;

@Data
public class XExcelEngine {
	public final static String _TimesNewRoman = "Times New Roman";
	public final static String _FormartNumber = "#,###.##";
	public final static String _FormartNumber_INT = "#,###";
	public final static short SIZE_HEADER = 12;
	public final static short SIZE_DETAIL = 11;
	private final static short SIZE_TITLE = 16;
	private final static short SIZE_NORM = 10;

	public short sheetnum;
	public String name;
	public XSSFWorkbook workBook;
	public XSSFSheet[] sheet;
	public short[] colnum;
	private final static short font_rate = 20;
	private final static short col_width = 0x1000;

	XSSFCellStyle cellStyle;
	XSSFCellStyle cellStyleHeader;
	XSSFCellStyle cellStyleTitle;
	XSSFCellStyle cellStyleCenter;
	XSSFCellStyle cellStyleRight;
	XSSFCellStyle cellStyleCenterFooter;
	XSSFCellStyle cellStyleFormatInt;
	XSSFCellStyle cellStyleFormatNum;
	XSSFCellStyle cellStyleFormatNumString;
	XSSFCellStyle cellStyleChung;
	XSSFCellStyle cellStyleFormatIntChung;
	XSSFCellStyle cellStyleFormatNumChung;
	XSSFCellStyle cellStyleFormatIntCenter;
	XSSFCellStyle cellStyleFormatNumCenter;
	XSSFCellStyle cellStyleTKHQBottom;

	public XExcelEngine(XSSFWorkbook workBook) {
		this.workBook = workBook;
		XSSFFont font = createFont(false, SIZE_DETAIL);
		XSSFFont fontTKHQBottom = createFontBottom(false, SIZE_DETAIL);
		XSSFFont fontHeader = createFont(true, SIZE_HEADER);
		XSSFFont fontTitle = createFont(true, SIZE_TITLE);
		XSSFFont fontNorm = createFont(false, SIZE_NORM);

		cellStyle = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), BorderStyle.THIN,
				true, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, font);

		cellStyleCenter = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, fontNorm);
		cellStyleRight = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), BorderStyle.THIN,
				true, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, font);

		cellStyleTitle = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, fontTitle);

		cellStyleHeader = createCellStyle(IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, fontHeader);

		cellStyleCenterFooter = createCellStyleBotton(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, font);

		cellStyleChung = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.LEFT, VerticalAlignment.TOP, font);

		cellStyleTKHQBottom = createCellStyleTKHQBottom(IndexedColors.WHITE.getIndex(),
				IndexedColors.GREY_25_PERCENT.getIndex(), BorderStyle.THIN, false, HorizontalAlignment.LEFT,
				VerticalAlignment.CENTER, fontTKHQBottom);

		cellStyleFormatInt = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, font, _FormartNumber_INT);

		cellStyleFormatIntChung = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.LEFT, VerticalAlignment.TOP, font, _FormartNumber_INT);

		cellStyleFormatIntCenter = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, font, _FormartNumber_INT);

		cellStyleFormatNum = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, font, _FormartNumber);

		cellStyleFormatNumChung = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.LEFT, VerticalAlignment.TOP, font, _FormartNumber);

		cellStyleFormatNumCenter = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, font, _FormartNumber);

		cellStyleFormatNumString = createCellStyle(IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(),
				BorderStyle.THIN, true, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, font);
	}

	public XSSFFont createFont(boolean bold_weight, short font_height, String font_name) {
		XSSFFont font = workBook.createFont();
		font.setFontName(font_name);
		font.setBold(bold_weight);
		font.setFontHeight((short) (font_height * font_rate));
		return font;
	}

	public XSSFFont createFont(boolean bold_weight, short font_height) {
		return createFont(bold_weight, false, font_height);
	}

	public XSSFFont createFontBottom(boolean bold_weight, short font_height) {
		return createFontBottom(bold_weight, false, font_height);
	}

	public XSSFFont createFont(boolean bold_weight, boolean italic, short font_height) {
		XSSFFont font = workBook.createFont();
		font.setFontName(_TimesNewRoman);
		font.setBold(bold_weight);
		font.setItalic(italic);
		font.setFontHeight((short) (font_height * font_rate));
		return font;
	}

	public XSSFFont createFont(boolean bold_weight, boolean italic, boolean wraptext, short font_height) {
		XSSFFont font = workBook.createFont();
		font.setFontName(_TimesNewRoman);
		font.setBold(bold_weight);
		font.setItalic(italic);
		font.setFontHeight((short) (font_height * font_rate));
		return font;
	}

	public XSSFFont createFontBottom(boolean bold_weight, boolean italic, short font_height) {
		XSSFFont font = workBook.createFont();
		font.setFontName(_TimesNewRoman);
		font.setBold(bold_weight);
		font.setItalic(true);
		font.setBold(true);
		font.setFontHeight((short) (font_height * font_rate));
		return font;
	}

	public XSSFCellStyle createCellStyle(short cellColor, short borderColor, BorderStyle borderStyle, boolean wrapText,
			HorizontalAlignment alignment, VerticalAlignment verticalAlign, XSSFFont font) {

		XSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(cellColor);
		cellStyle.setFillBackgroundColor(cellColor);
		cellStyle.setAlignment(alignment);
		cellStyle.setVerticalAlignment(verticalAlign);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBottomBorderColor(borderColor);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setLeftBorderColor(borderColor);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setRightBorderColor(borderColor);
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setTopBorderColor(borderColor);
		cellStyle.setWrapText(wrapText);
		cellStyle.setFont(font);
		return cellStyle;
	}

	public XSSFCellStyle createCellStyleBotton(short cellColor, short borderColor, BorderStyle borderStyle,
			boolean wrapText, HorizontalAlignment alignment, VerticalAlignment verticalAlign, XSSFFont font) {

		XSSFCellStyle cellStyle = workBook.createCellStyle();
		byte[] rgb;
		XSSFColor color;
		rgb = new byte[3];
		rgb[0] = (byte) 189; // red
		rgb[1] = (byte) 215; // green
		rgb[2] = (byte) 238; // blue
		color = new XSSFColor();
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(color);
		cellStyle.setAlignment(alignment);
		cellStyle.setVerticalAlignment(verticalAlign);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBottomBorderColor(borderColor);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setLeftBorderColor(borderColor);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setRightBorderColor(borderColor);
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setTopBorderColor(borderColor);
		cellStyle.setWrapText(wrapText);
		cellStyle.setFont(font);
		return cellStyle;
	}

	public XSSFCellStyle createCellStyleTKHQBottom(short cellColor, short borderColor, BorderStyle borderStyle,
			boolean wrapText, HorizontalAlignment alignment, VerticalAlignment verticalAlign, XSSFFont font) {

		XSSFCellStyle cellStyle = workBook.createCellStyle();
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(cellColor);
		cellStyle.setFillBackgroundColor(cellColor);
		cellStyle.setAlignment(alignment);
		cellStyle.setVerticalAlignment(verticalAlign);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBottomBorderColor(borderColor);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setLeftBorderColor(borderColor);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setRightBorderColor(borderColor);
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setTopBorderColor(borderColor);
		cellStyle.setWrapText(wrapText);
		cellStyle.setFont(font);
		return cellStyle;
	}

	public XSSFCellStyle createCellStyle(short cellColor, short borderColor, BorderStyle borderStyle, boolean wrapText,
			HorizontalAlignment alignment, VerticalAlignment verticalAlign, XSSFFont font, String formatString) {

		XSSFCellStyle cellStyle = workBook.createCellStyle();
		XSSFDataFormat format = workBook.createDataFormat();
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(cellColor);
		cellStyle.setFillBackgroundColor(cellColor);
		cellStyle.setAlignment(alignment);
		cellStyle.setVerticalAlignment(verticalAlign);
		cellStyle.setBorderBottom(borderStyle);
		cellStyle.setBottomBorderColor(borderColor);
		cellStyle.setBorderLeft(borderStyle);
		cellStyle.setLeftBorderColor(borderColor);
		cellStyle.setBorderRight(borderStyle);
		cellStyle.setRightBorderColor(borderColor);
		cellStyle.setBorderTop(borderStyle);
		cellStyle.setTopBorderColor(borderColor);
		cellStyle.setWrapText(wrapText);
		cellStyle.setFont(font);
		cellStyle.setDataFormat(format.getFormat(formatString));
		return cellStyle;
	}

	public XSSFCell createCell(XSSFRow row, short columnIndex, XSSFCellStyle cellStyle, String content) {
		XSSFCell cell = row.createCell(columnIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(new XSSFRichTextString(content));
		return cell;
	}

	public static XSSFCell createCell(XSSFRow row, short columnIndex, XSSFCellStyle cellStyle, double content) {
		XSSFCell cell = row.createCell(columnIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(content);
		return cell;
	}

	public XSSFWorkbook getWorkBook() {
		return this.workBook;
	}

	public void setWorkBook(XSSFWorkbook workbook) {
		this.workBook = workbook;
	}

	public static XSSFRow createRow(XSSFSheet sheet, int rowIndex, short rowHeight) {
		XSSFRow row = sheet.createRow(rowIndex);
		row.setHeight(rowHeight);
		return row;
	}

	public static XSSFRow createRow(XSSFSheet sheet, int rowIndex) {
		XSSFRow row = sheet.createRow(rowIndex);
		return row;
	}

	public XSSFCell createCellText(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(new XSSFRichTextString(content == null ? "" : content));
		return cell;
	}

	public XSSFCell createCellTextCenter(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleCenter);
		cell.setCellValue(new XSSFRichTextString(content == null ? "" : content));
		return cell;
	}

	public XSSFCell createCellTextCenterFooter(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleCenterFooter);
		cell.setCellValue(new XSSFRichTextString(content == null ? "" : content));
		return cell;
	}

	public XSSFCell createCellDate(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(content == null ? "" : content);
		return cell;
	}

	public XSSFCell createCellNumber(XSSFCellStyle cellStyle, XSSFRow row, int cellIndex, double content) {
		XSSFCell cell = row.createCell(cellIndex);
		if (isInteger(content)) {
			cell.setCellStyle(cellStyleFormatInt);
		} else {
			cell.setCellStyle(cellStyleFormatNum);
		}
		cell.setCellValue(content);
		return cell;
	}

	public XSSFCell createCellNumber(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		if (org.apache.commons.lang3.math.NumberUtils.isNumber(content)) {
			if (isInteger(Double.valueOf(content))) {
				cell.setCellStyle(cellStyleFormatInt);
			} else {
				cell.setCellStyle(cellStyleFormatNum);
			}
			cell.setCellValue(Double.valueOf(content));
		} else {
			cell.setCellStyle(cellStyle);
			cell.setCellValue("");
		}
		return cell;
	}

	public XSSFCell createCellNumber(XSSFRow row, int cellIndex, Number content) {
		XSSFCell cell = row.createCell(cellIndex);
		if (content == null) {
			cell.setCellStyle(cellStyle);
			cell.setCellValue("");
		} else {
			if (isInteger(content.doubleValue())) {
				cell.setCellStyle(cellStyleFormatInt);
			} else {
				cell.setCellStyle(cellStyleFormatNum);
			}
			cell.setCellValue(content.doubleValue());
		}
		return cell;
	}

	private boolean isInteger(double d) {
		return (d == Math.floor(d)) && !Double.isInfinite(d);
	}

	protected void download() throws IOException {
//		OutputStream out;
//		try {
//			out = response.getOutputStream();
//
//			response.reset();
//			response.setContentType("application/vnd.ms-excel");
//			response.setHeader("Content-disposition", "attachment;filename=" + name + ".xlsx");
//			workBook.write(out);
//			out.flush();
//			out.close();
//		} catch (IOException ioEx) {
//			ioEx.printStackTrace();
//			throw ioEx;
//		}
	}

	public XSSFCell createCellNumberChung(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		if (org.apache.commons.lang3.math.NumberUtils.isNumber(content)) {
			if (isInteger(Double.valueOf(content))) {
				cell.setCellStyle(cellStyleFormatIntChung);
			} else {
				cell.setCellStyle(cellStyleFormatNumChung);
			}
			cell.setCellValue(Double.valueOf(content));
		} else {
			cell.setCellStyle(cellStyleChung);
			cell.setCellValue("");
		}
		return cell;
	}

	public XSSFCell createCellNumberChung(XSSFRow row, int cellIndex, Number content) {
		XSSFCell cell = row.createCell(cellIndex);
		if (content == null) {
			cell.setCellStyle(cellStyleChung);
			cell.setCellValue("");
		} else {
			if (isInteger(content.doubleValue())) {
				cell.setCellStyle(cellStyleFormatIntChung); /* cellStyleFormatIntChung */
			} else {
				cell.setCellStyle(cellStyleFormatNumChung); /* cellStyleFormatNumChung */
			}
			cell.setCellValue(content.doubleValue());
		}
		return cell;
	}

	public XSSFCell createCellNumberCenter(XSSFRow row, int cellIndex, Number content) {
		XSSFCell cell = row.createCell(cellIndex);
		if (content == null) {
			cell.setCellStyle(cellStyleCenter);
			cell.setCellValue("");
		} else {
			if (isInteger(content.doubleValue())) {
				cell.setCellStyle(cellStyleFormatIntCenter);
			} else {
				cell.setCellStyle(cellStyleFormatNumCenter);
			}
			cell.setCellValue(content.doubleValue());
		}
		return cell;
	}

	public XSSFCell createCellTextChung(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleChung);
		cell.setCellValue(new XSSFRichTextString(content == null ? "" : content));
		return cell;
	}

	public XSSFCell createCellTextNumber(XSSFRow row, int cellIndex, Long content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleChung);
		cell.setCellValue(content == null ? 0 : content);
		return cell;
	}

	public XSSFCell createCellDateChung(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleChung);
		cell.setCellValue(content == null ? "" : content);
		return cell;
	}

	public XSSFCell createCellTextTKHQBottom(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleTKHQBottom);
		cell.setCellValue(new XSSFRichTextString(content == null ? "" : content));
		return cell;
	}

	public XSSFCell createCellTextTKHQOutBottom(XSSFRow row, int cellIndex, String content) {
		XSSFCell cell = row.createCell(cellIndex);
		cell.setCellStyle(cellStyleTKHQBottom);
		cell.setCellValue(new XSSFRichTextString(content == null ? "" : content));
		return cell;
	}

//	public static CellStyle getCellStyleNorm(Workbook workbook) {
//		CellStyle cellStyle = workbook.createCellStyle();
//		cellStyle.setBorderTop(BorderStyle.THIN);
//		cellStyle.setBorderBottom(BorderStyle.THIN);
//		cellStyle.setBorderLeft(BorderStyle.THIN);
//		cellStyle.setBorderRight(BorderStyle.THIN);
//		cellStyle.setAlignment(HorizontalAlignment.CENTER);
//		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		return cellStyle;
//	}

	public void setTitle(Sheet sheet, CellStyle style, String title) {
		Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(title);
		row.getCell(0).setCellStyle(style);
	}
	public void setCell(Row row, String cellValue, CellStyle style, int index) {
		row.createCell(index).setCellValue(cellValue == null ? "" : cellValue);
		row.getCell(index).setCellStyle(style);
	}

	public void setCellCenter(Row row, String cellValue, int index) {
		row.createCell(index).setCellValue(cellValue == null ? "" : cellValue);
		row.getCell(index).setCellStyle(cellStyleCenter);
	}

	public void createHeader(Sheet sheet, List<String> lstHeader, int rowNum) {
		Row row = sheet.createRow(rowNum);
		for (int i = 0; i < lstHeader.size(); i++) {
			sheet.autoSizeColumn(i);
			row.createCell(i).setCellValue(lstHeader.get(i));
			row.getCell(i).setCellStyle(cellStyleHeader);
		}
	}
}