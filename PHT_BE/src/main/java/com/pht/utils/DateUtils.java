package com.pht.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

public class DateUtils {
	private static final String THIS = "DateUtils";

	public static XMLGregorianCalendar convertDate(Date serviceDate) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(serviceDate);
		try {
			XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			// remove time zone 2017-04-18T00:00:00+02:00 -> 2017-04-18T00:00:00
			xmlDate.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
			xmlDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			return xmlDate;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException();
		}
	}

	public static Date toDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	/**
	 * Convert from number to Date value with pattern "yyyyMMdd"
	 * 
	 * @param numDate number to convert
	 * @return {@link Date}
	 */
	public static Date fromNumDate(long numDate) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(numDate + "", "yyyyMMdd");
		} catch (ParseException e) {
			return null;
		}
	}

	public static String convertDate(String originDate, String fromDateFormat, String toDateFormat) {
		SimpleDateFormat originalFormatter = new SimpleDateFormat(fromDateFormat);
		SimpleDateFormat newFormatter = new SimpleDateFormat(toDateFormat);
		ParsePosition pos = new ParsePosition(0);
		Date dateFromString = originalFormatter.parse(originDate, pos);
		String result = newFormatter.format(dateFromString);
		return result;
	}

	public static String formatDate(Date date, String sDateFormatin) {
		String sOutput = "";
		String sDateFormat = sDateFormatin;
		if (sDateFormatin.equalsIgnoreCase("Q"))
			sDateFormat = "Q/yyyy";

		if (sDateFormatin.equalsIgnoreCase("M"))
			sDateFormat = "MM/yyyy";

		if (sDateFormatin.equalsIgnoreCase("Y"))
			sDateFormat = "yyyy";

		if (date != null) {
			if (sDateFormat.equalsIgnoreCase("Q/yyyy")) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				int m = c.get(Calendar.MONTH);

				if (m <= Calendar.MARCH)
					sOutput = "Q1/" + c.get(Calendar.YEAR);
				else if (m <= Calendar.JUNE)
					sOutput = "Q2/" + c.get(Calendar.YEAR);
				else if (m <= Calendar.SEPTEMBER)
					sOutput = "Q3/" + c.get(Calendar.YEAR);
				else
					sOutput = "Q4/" + c.get(Calendar.YEAR);
			} else
				sOutput = (new SimpleDateFormat(sDateFormat)).format(date);
		}
		return sOutput;
	}

	public static boolean isDate(String dateStr, String format) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setLenient(false);
			dateFormat.parse(dateStr);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean greaterDate(String dateStr, String dateStr1, String format) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat(format);
			if (dateFormat1.parse(dateStr1).equals(dateFormat.parse(dateStr))) {
				return false;
			} else {
				if (!dateFormat1.parse(dateStr1).after(dateFormat.parse(dateStr))) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception ex) {
			return false;
		}
	}

	public static Date parseMultipleFormatDate(String strDate) throws ParseException {
		String[] frmDate = new String[] { "yyyy-MM-dd", "dd-MM-yyyy", "yyyy/MM/dd", "dd/MM/yyyy", "yyyyMMdd" };
		return org.apache.commons.lang3.time.DateUtils.parseDate(strDate, frmDate);
	}

//	public static String formatDate(DateTime dt, String pattern){
//		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
//		return fmt.print(dt);
//	}
	public static String convertCDDate(String cdDate) {
		try {
			Date tmPDate = org.apache.commons.lang3.time.DateUtils.parseDate(cdDate,
					new String[] { "yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy HH:mm:ss" });
			cdDate = DateFormatUtils.format(tmPDate, "dd/MM/yyyy HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cdDate;
	}

	public static String convertDate2String(Date date, String format) {
		String strDate = "";

		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			strDate = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDate;
	}

	public static String ConvertStringToDate(String stringDate, String patternSource, String patternDestionation)
			throws Exception {

		if (stringDate == null || stringDate.length() == 0) {
			return null;
		} else {
			final String location = THIS + ".stringToDate(" + stringDate + "," + patternSource + ")";
			try {
				SimpleDateFormat sdfSource = new SimpleDateFormat(patternSource);
				sdfSource.setLenient(false);
				Date date = sdfSource.parse(stringDate);
				SimpleDateFormat sdfDestination = new SimpleDateFormat(patternDestionation);
				sdfDestination.setTimeZone(TimeZone.getTimeZone("GMT+7"));
				stringDate = sdfDestination.format(date);
				return stringDate;
			} catch (ParseException e) {
				throw new Exception(location + " - Can not convert to Date due to wrong format");
			}
		}
	}

	public static String convertDateToString(java.util.Date date, String datePattern) {
		if (date == null) {
			return null;
		} else {
			final String loc = THIS + ".dateToString(" + date + "," + datePattern + ")";
			try {
				SimpleDateFormat fmt = new SimpleDateFormat(datePattern);
				fmt.setTimeZone(TimeZone.getTimeZone("GMT+7"));
				return fmt.format(date);
			} catch (Exception e) {
				return "";
			}
		}
	}

	public static String getYearOfDate() {
		LocalDate today = LocalDate.now();

		// Lấy năm từ ngày hiện tại
		int year = today.getYear();
		return Integer.toString(year).substring(2, 4);
	}

	public static String convertDateTime(String date, String fromFormar, String toFormat) throws Exception {// HH:mm
		try {
			SimpleDateFormat df = new SimpleDateFormat(fromFormar);
			df.setTimeZone(TimeZone.getTimeZone("GMT+7"));
			Date strDate = df.parse(date);
			SimpleDateFormat df1 = new SimpleDateFormat(toFormat);
			df1.setTimeZone(TimeZone.getTimeZone("GMT+7"));
			return df1.format(strDate);
		} catch (Exception e) {
			throw e;
		}
	}

	public static Date convertStringToDate(String stringDate, String pattern) throws ParseException {
		if (stringDate == null || stringDate.length() == 0) {
			return null;
		} else {
			final String location = THIS + ".stringToDate(" + stringDate + "," + pattern + ")";

			try {
				SimpleDateFormat fm = new SimpleDateFormat(pattern);
				fm.setTimeZone(TimeZone.getTimeZone("GMT+7"));
				fm.setLenient(false);

				return new java.sql.Date(fm.parse(stringDate).getTime());
			} catch (ParseException e) {
				throw e;
			}
		}
	}

	public static String ConvertStringDNtoDateFormat(String dateStringInOriginalFormat) {
		SimpleDateFormat originalFormatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy");
		newFormatter.setTimeZone(TimeZone.getTimeZone("GMT+7"));
		ParsePosition pos = new ParsePosition(0);
		Date dateFromString = originalFormatter.parse(dateStringInOriginalFormat, pos);
		String dateStringInNewFormat = newFormatter.format(dateFromString);
		return dateStringInNewFormat;
	}

	public static String doubleToStringWithoutDecimal(double d) {
		DecimalFormatSymbols symbol = new DecimalFormatSymbols();
		symbol.setGroupingSeparator(' ');
		symbol.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#,###", symbol);
		return format.format(d);
	}

	public static String doubleToStringWithoutDecimal(String d) {
		DecimalFormatSymbols symbol = new DecimalFormatSymbols();
		symbol.setGroupingSeparator(' ');
		symbol.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#,###", symbol);
		return format.format(org.apache.commons.lang3.math.NumberUtils
				.toDouble(org.apache.commons.lang3.StringUtils.replace(d, " ", "")));
	}

	public static String dateToString(LocalDate date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return date.format(formatter);
	}

	public static Integer getSysDateInt() {
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String outputDateStr = LocalDate.now().format(outputFormatter);

		return Integer.parseInt(outputDateStr);
	}

	public static Integer dateToInt(LocalDate date) {
		if (date == null)
			return null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = date.format(formatter);

		return Integer.parseInt(formattedDate);
	}

	public static Integer strDateToInt(String strDate) {
		if (ValidationUtils.isNullOrEmpty(strDate))
			return null;
		LocalDate date = stringToLocalDate(strDate, "dd/MM/yyyy");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formattedDate = date.format(formatter);

		return Integer.parseInt(formattedDate);
	}

	public static String dateToString(String fomat) {
		LocalDate today = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fomat);
		String formattedDate = today.format(formatter);

		return formattedDate;
	}

	public static String doubleToStringWithoutDecimalE(String d) {
		return doubleToStringWithoutDecimal(d).replace(" ", "");
	}

	public static String ConvertDateFormattoStringDN(String dateStringInOriginalFormat) {
		SimpleDateFormat originalFormatter = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat newFormatter = new SimpleDateFormat("yyyyMMdd");
		newFormatter.setTimeZone(TimeZone.getTimeZone("GMT+7"));
		ParsePosition pos = new ParsePosition(0);
		Date dateFromString = originalFormatter.parse(dateStringInOriginalFormat, pos);
		String dateStringInNewFormat = newFormatter.format(dateFromString);
		return dateStringInNewFormat;
	}

	public static String paddingString(String s, int n, char c, boolean paddingLeft) {
		if (s == null) {
			return s;
		}
		int add = n - s.length();
		if (add <= 0) {
			return s;
		}
		StringBuffer str = new StringBuffer(s);
		char[] ch = new char[add];
		Arrays.fill(ch, c);
		if (paddingLeft) {
			str.insert(0, ch);
		} else {
			str.append(ch);
		}
		return str.toString();
	}

	public static String formatDateKC(String strDate, String format) {
		String strRet = null;
		String checkstr = "0123456789";
		String DateValue = "";
		String DateTemp = "";
		String seperator = "/";
		String day;
		String month;
		String year;
		String h;
		String phut;
		String s;

		int i;

		DateValue = strDate;
		if (DateValue == "" || DateValue.length() <= 0) {
			return strRet;
		}
		/* Delete all chars except 0..9 */
		for (i = 0; i < DateValue.length(); i++) {
			if (checkstr.indexOf(DateValue.substring(i, i + 1)) >= 0) {
				DateTemp = DateTemp + DateValue.substring(i, i + 1);
			}
		}
		DateValue = DateTemp;

		/* year is wrong if year = 0000 */
		year = DateValue.substring(0, 4);

		/* Validation of month */
		month = DateValue.substring(4, 6);
		day = DateValue.substring(6, 8);
		h = DateValue.substring(8, 10);
		phut = DateValue.substring(10, 12);
		s = DateValue.substring(12, 14);
		if ("DD/MM/YYYY".equalsIgnoreCase(format.toUpperCase())) {
			DateValue = day + seperator + month + seperator + year;
		} else if ("MM/DD/YYYY".equalsIgnoreCase(format.toUpperCase())) {
			DateValue = month + seperator + day + seperator + year;
		} else if ("YYYY/MM/DD".equalsIgnoreCase(format.toUpperCase())) {
			DateValue = year + seperator + month + seperator + day;
		} else if ("dd/MM/yyyy/TK".equalsIgnoreCase(format.toUpperCase())) {
			DateValue = year + month + day;
		} else if ("dd/MM/yyyy hh24:mi:ss".equalsIgnoreCase(format.toUpperCase())) {
			DateValue = day + "/" + month + "/" + year + " " + h + ":" + phut + ":" + s;
		} else if ("dd/MM/yyyy".equalsIgnoreCase(format.toUpperCase())) {
			DateValue = day + "/" + month + "/" + year;
		} else {
			DateValue = day + seperator + month + seperator + year;
		}
		strRet = DateValue;
		return strRet;
	}

	public static Date localDateToDate(LocalDate localDate) {
		if (ValidationUtils.isNullOrEmpty(localDate)) {
			return null;
		}
		LocalDateTime localDateTime = localDate.atStartOfDay();
		// Chuyển LocalDateTime thành Date
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		if (ValidationUtils.isNullOrEmpty(localDateTime)) {
			return null;
		}
		// Chuyển LocalDateTime thành Date
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static String doubleToString(double dSo_tien) {
		// lam tron ve 2 so thap phan
		double dSotienRound = Math.round(dSo_tien * 100.0) / 100.0;

		String sSo_tien = String.valueOf(dSotienRound);
		String sGia_tri_tra_ve = "";
		int iEPos = sSo_tien.indexOf("E");
		int iCommaPos = sSo_tien.indexOf(".");
		int iSo_mu = 0;
		if (dSotienRound == 0.0D)
			return "0";
		if (sSo_tien == null)
			return "";
		if (iEPos <= 0) {
			if (sSo_tien.indexOf(".") > 0)
				sGia_tri_tra_ve = sSo_tien;
			else
				sGia_tri_tra_ve = sSo_tien;
			sGia_tri_tra_ve = formatCurrency_Tcs(sGia_tri_tra_ve);
			return sGia_tri_tra_ve;
		}
		iSo_mu = Integer.parseInt(sSo_tien.substring(iEPos + 1, sSo_tien.length()));
		if (iCommaPos <= 0) {
			sGia_tri_tra_ve = sSo_tien.substring(0, iEPos);
			for (int i = 0; i < iSo_mu; i++)
				sGia_tri_tra_ve = sGia_tri_tra_ve + "0";

		} else {
			int iSo_sau_cham = iEPos - (iCommaPos + 1);
			int iHieu_so = iSo_mu - iSo_sau_cham;
			if (iHieu_so == 0)
				sGia_tri_tra_ve = sSo_tien.substring(0, iCommaPos) + sSo_tien.substring(iCommaPos + 1, iEPos);
			else if (iHieu_so > 0) {
				sGia_tri_tra_ve = sSo_tien.substring(0, iCommaPos) + sSo_tien.substring(iCommaPos + 1, iEPos);
				for (int i = 0; i < iHieu_so; i++)
					sGia_tri_tra_ve = sGia_tri_tra_ve + "0";

			} else {
				sGia_tri_tra_ve = sSo_tien.substring(0, iCommaPos)
						+ sSo_tien.substring(iCommaPos + 1, iCommaPos + 1 + iSo_mu) + "."
						+ sSo_tien.substring(iCommaPos + iSo_mu + 1, iEPos);
			}
		}
		sGia_tri_tra_ve = formatCurrency_Tcs(sGia_tri_tra_ve);
		return sGia_tri_tra_ve;
	}

	public static String formatCurrency_Tcs(String inValue) {
		if (inValue == null) {
			return "";
		}
		if (inValue.equals("")) {
			return "";
		}
		int i = inValue.indexOf(".");
		String outValue = "";
		if (i != -1) {
			outValue = inValue.substring(i);
			inValue = inValue.substring(0, i);
		}
		while (inValue.length() > 3) {
			outValue = " " + inValue.substring(inValue.length() - 3, inValue.length()) + outValue;
			inValue = inValue.substring(0, inValue.length() - 3);
		}
		if (inValue.equals("") || inValue.equals("-")) {
			outValue = inValue + outValue.substring(1);
		} else {
			outValue = inValue + outValue;
		}
		return outValue;
	}

	public static String doubleToStringWithoutE(double d, int length) {
		if (d == 0)
			return "0";
		DecimalFormat df = new DecimalFormat(".#");
		df.setMaximumFractionDigits(length);
		String returns = df.format(d);
		int i = returns.indexOf(".");
		if (i > 0) {
			String temp1 = returns.substring(0, i);
			String temp2 = returns.substring(i + 1);
			try {
				if (Double.parseDouble(temp2) == 0) {
					returns = temp1;
				}
			} catch (Exception e) {
				returns = temp1;
			}
		}
		return returns;
	}

	public static Date addDay(Date date, int num) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, num);
			date = c.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Chuyển date dạng int yyyyMMdd sang Localdate
	 * 
	 * @param input số dạng yyyyMMdd
	 * @return Local Date
	 */
	public static LocalDate convertIntToDate(Integer input) {
		String inputDateStr = String.valueOf(input);

		// Định dạng đầu vào và đầu ra
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		return LocalDate.parse(inputDateStr, inputFormatter);
	}

	/**
	 * Convert LocalDate thành chuỗi
	 * 
	 * @param date    LocalDate
	 * @param pattern Định dạng chuỗi trả về
	 * @return chuỗi date có định dạng truyền vào
	 */
	public static String localDateToString(LocalDate date, String pattern) {
		if (ValidationUtils.isNullOrEmpty(date)) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return date.format(formatter);
	}

	public static String convertFromDateFormatToString_DaoNguoc(String strDate) {
		String strPerfect = "";
		strDate = strDate.replaceAll("/", "");
		strPerfect = strDate.substring(4, 8) + strDate.substring(2, 4) + strDate.substring(0, 2);
		return strPerfect;
	}

	public static String formatCurrentDate() {
		// Tạo đối tượng SimpleDateFormat với định dạng riêng cho ngày, tháng, năm
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

		// Lấy ngày hiện tại
		Date now = new Date();

		// Định dạng ngày, tháng, năm
		String day = dayFormat.format(now);
		String month = monthFormat.format(now);
		String year = yearFormat.format(now);

		// Xây dựng chuỗi theo định dạng mong muốn
		return String.format("Ngày %s tháng %s năm %s", day, month, year);
	}

	/*
	 * set xref core folow ngay_kb
	 */
	public static String getTransIdDate(String loaiCT, String soCT, String ngay_kb) throws ParseException {
		StringBuilder result = new StringBuilder("TCS");

		Calendar cal = Calendar.getInstance();
		cal.setTime(org.apache.commons.lang3.time.DateUtils.parseDate(ngay_kb, new String[] { "yyyyMMdd" }));
		int year = cal.get(Calendar.YEAR);
		int day_year = cal.get(Calendar.DAY_OF_YEAR);
		String yearStr = String.valueOf(year).substring(2, 4);
		result.append(yearStr);
		result.append(String.valueOf(day_year));
		if (loaiCT.equals("BBN")) {
			result.append("3");
		} else {
			result.append("1");
		}
		if (soCT.length() > 7) {
			result.append(soCT.substring(soCT.length() - 7, soCT.length()));
		} else {
			result.append(StringUtils.leftPad(soCT, 7, '0'));
		}
		return result.toString();
		// TCS1730410000551
	}

	public static String getPreDate(String dateString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// Phân tích chuỗi thành đối tượng Date
		Date date = sdf.parse(dateString);

		// Sử dụng Calendar để trừ đi 1 ngày
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1); // Trừ 1 ngày

		// Lấy ngày đã điều chỉnh
		Date previousDate = calendar.getTime();

		// Chuyển đổi lại thành chuỗi và trả ra
		return sdf.format(previousDate);
	}

	public static LocalDate stringToLocalDate(String dateString, String pattern) {
		if (ValidationUtils.isNullOrEmpty(dateString)) {
			return null;
		}
		// Định dạng cần sử dụng để phân tích chuỗi
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDate.parse(dateString, formatter);
	}

	public static Date intToDate(Integer numDate) {
		try {
			return org.apache.commons.lang3.time.DateUtils.parseDate(numDate + "", "yyyyMMdd");
		} catch (ParseException e) {
			return null;
		}
	}

	public static XMLGregorianCalendar intToXMLGregorianCalendar(Integer dateNum) {
		if (ValidationUtils.isNullOrEmpty(dateNum)) {
			return null;
		}
		Date serviceDate = intToDate(dateNum);
		return convertDate(serviceDate);
	}

	public static XMLGregorianCalendar stringToXMLGregorianCalendar(String dateNum) throws ParseException {
		if (ValidationUtils.isNullOrEmpty(dateNum)) {
			return null;
		}
		Date serviceDate = parseMultipleFormatDate(dateNum);
		return convertDate(serviceDate);
	}

	public static String convertXMLGregorianCalendarToString(XMLGregorianCalendar xmlGregorianCalendar, String format) {
		if (ValidationUtils.isNullOrEmpty(xmlGregorianCalendar)) {
			return null;
		}
		// Chuyển đổi XMLGregorianCalendar thành Date
		Date date = xmlGregorianCalendar.toGregorianCalendar().getTime();

		// Định dạng chuỗi
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static XMLGregorianCalendar localDateTimeToXMLGregorianCalendar(LocalDateTime localDateTime) {
		if (ValidationUtils.isNullOrEmpty(localDateTime)) {
			return null;
		}
		try {
			return DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(localDateTime.atZone(ZoneId.systemDefault()).toLocalDateTime().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static LocalDateTime parseStringToLocalDateTime(String dateTimeString) {
		try {
			return LocalDateTime.parse(dateTimeString);
		} catch (DateTimeParseException e) {
			System.err.println("Lỗi khi phân tích chuỗi: " + e.getMessage());
			return null;
		}
	}

	public static int getYearFromDateString(String dateString) {
		try {
			// Chuyển đổi chuỗi thành LocalDate
			LocalDate date = LocalDate.parse(dateString);
			// Lấy năm
			return date.getYear();
		} catch (DateTimeParseException e) {
			System.err.println("Lỗi khi phân tích chuỗi ngày: " + e.getMessage());
			return -1;
		}
	}

	public static String convertAmount(String input) {
		String formattedNumber = input;
		try {
			long number = Long.parseLong(input); // Chuyển đổi sang số nguyên

			NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMANY);
			formattedNumber = numberFormat.format(number);
		} catch (DateTimeParseException e) {
			System.err.println("Lỗi convert: " + e.getMessage());
		}
		return formattedNumber;
	}

	// test
	public static void main(String[] args) throws ParseException {
		System.out.println(LocalDate.parse("01/07/2024"));

	}

	public static String dateNow(String format) {
		// Get current date
		Date currentDate = new Date();

		// Create a SimpleDateFormat object with the desired format
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		// Format the current date
		String formattedDate = sdf.format(currentDate);

		// Print the formatted date
		return formattedDate;
	}

	public static LocalDateTime getPreviousDay(String time) {
		// Lấy ngày hôm nay
		LocalDateTime now = LocalDateTime.now();

		// Trừ 1 ngày từ ngày hôm nay để lấy ngày hôm trước
		LocalDateTime previousDay = now.minusDays(1);

		// Cập nhật thời gian thành 16:00:00 (4:00 PM)
		LocalDateTime previous_Day = previousDay.withHour(Integer.parseInt(time.split(":")[0]))
				.withMinute(Integer.parseInt(time.split(":")[1])).withSecond(Integer.parseInt(time.split(":")[0]))
				.withNano(0);

		return previous_Day;
	}

	public static LocalDateTime getToDay(String time) {
		// Lấy ngày hôm nay
		LocalDateTime now = LocalDateTime.now();

		// Cập nhật thời gian thành 16:00:00 (4:00 PM) của ngày hôm nay
		LocalDateTime today = now.withHour(Integer.parseInt(time.split(":")[0]))
				.withMinute(Integer.parseInt(time.split(":")[1])).withSecond(Integer.parseInt(time.split(":")[0]))
				.withNano(0);

		return today;
	}

}
