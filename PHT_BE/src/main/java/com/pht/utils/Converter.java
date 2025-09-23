/*
 * Created on Aug 2, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.pht.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author ThanhDX
 * @since 02/8/2006
 * Tap hop cac ham cua hai Class ConvertFromString va ConvertToString
 * thanh lop Converter.
 *
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Date;

/**
 * @author ThanhDX
 * @since 02/8/2006 Tap hop cac ham cua hai Class ConvertFromString va
 *        ConvertToString thanh lop Converter.
 * 
 *        Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Converter {
	public static Calendar formatToCalendar(String sDate) throws Exception {
		Calendar cal = formatToCalendar(sDate, "dd/MM/yyyy");
		if (cal == null)
			cal = formatToCalendar(sDate, "dd/MM/yyyy");
		return cal;
	}

	public static Calendar formatToCalendar(String sDate, String sDateTimeFormat) throws Exception {
		Calendar cal = null;
		if (sDate != null) {
			cal = Calendar.getInstance();
			cal.setTime((new SimpleDateFormat(sDateTimeFormat)).parse(sDate));
		}
		return cal;
	}

	public static java.sql.Date formatToSQLDate(String sDate) throws Exception {
		String format = getDateFormat();
		switch (sDate.length()) {
		case 10:
			format = "dd/MM/yyyy";
			break;
		case 4:
			format = "yyyy";
			break;
		case 7:
			if ("Q".equals(sDate.substring(0, 1))) {
				format = "Q/yyyy";
			} else {
				format = "MM/yyyy";
			}
			break;
		}
		java.sql.Date date = formatToSQLDate(sDate, format);
		return date;
	}

	public static java.sql.Date formatToSQLDate(String sDate, String sDateTimeFormat) throws Exception {
		java.sql.Date date = null;
		int q;
		int mon;
		String strMon;
		String strYar;

		if ("Q/yyyy".equals(sDateTimeFormat)) {
			q = Integer.parseInt(sDate.substring(1, 2));
			mon = q * 3 - 2;
			strYar = sDate.substring(sDate.indexOf("/") + 1);
			if (mon < 10) {
				strMon = "0" + Integer.toString(mon);
			} else {
				strMon = Integer.toString(mon);
			}
			sDate = strMon + "/" + strYar;
			sDateTimeFormat = "MM/yyyy";
		}

		if (sDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime((new SimpleDateFormat(sDateTimeFormat)).parse(sDate));
			date = new java.sql.Date(cal.getTime().getTime());
		}
		return date;
	}

	public static String getDateFormat() {
		return "dd/MM/yyyy";
	}

	/**
	 * 
	 * Thu tuc chuyen doi xau gia tri so sang gia tri Double Su dung khi get xau gia
	 * tri so tu HTML
	 * 
	 * @author cuongnd
	 * @since 28/03/2003
	 * @param String inData Xau chua gia tri so
	 * @return Double
	 * 
	 */
	public static Double formatToDouble(String inData) throws Exception {
		boolean isNegative = false;
		if (inData.indexOf("-") == 0) {
			inData = inData.substring(1);
			isNegative = true;
		}
		if (inData == null)
			return null;
		// Locale[] locales = NumberFormat.getAvailableLocales();
		Double theValue = null;
		NumberFormat form;
		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.GERMAN);

		if (isNegative)
			theValue = new Double(-1 * form.parse(inData).doubleValue());
		else
			theValue = new Double(form.parse(inData).doubleValue());
		return theValue;
	}

	/**
	 * 
	 * Thu tuc chuyen doi xau gia tri so sang gia tri Long Su dung khi get xau gia
	 * tri so tu HTML
	 * 
	 * @author cuongnd
	 * @since 28/03/2003
	 * @param String inData Xau chua gia tri so
	 * @return Long
	 * 
	 */
	public static Integer formatToInteger(String inData) throws Exception {

		if (inData == null || inData.equalsIgnoreCase(""))
			return null;
		// Locale[] locales = NumberFormat.getAvailableLocales();
		Integer theValue = null;
		NumberFormat form;

		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.GERMAN);
		theValue = new Integer(form.parse(inData).intValue());
		return theValue;
	}

	public static Long formatToLong(String inData) throws Exception {
		boolean isNegative = false;
		if (inData.indexOf("-") == 0) {
			inData = inData.substring(1);
			isNegative = true;
		}
		if (inData == null || inData.equalsIgnoreCase(""))
			return null;
		// Locale[] locales = NumberFormat.getAvailableLocales();
		Long theValue = null;
		NumberFormat form;

		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.GERMAN);
		if (isNegative)
			theValue = new Long(-1 * form.parse(inData).longValue());
		else
			theValue = new Long(form.parse(inData).longValue());
		return theValue;
	}

	/**
	 * 
	 * Thu tuc chuyen doi xau sang gia tri Boolean Su dung khi get xau tu HTML
	 * 
	 * @author cuongnd
	 * @since 28/03/2003
	 * @param String inData Xau chua gia boolean
	 * @return Boolean
	 * 
	 */
	public static Boolean formatToBoolean(String inData) {
		if (inData != null && inData.equals("Y"))
			return new Boolean(true);
		else
			return new Boolean(false);
	}

	/**
	 * 
	 * Thu tuc bo dau "," cua xau Double de chuyen sang dang Number ##,##->#### Su
	 * dung khi get xau tu HTML
	 * 
	 * @author longbh
	 * @since 05/05/2003
	 * @param String inData Xau chua gia tri Double co dang ###,###,###
	 * @return Boolean
	 * 
	 */
	public static String DoubleToString(String inData) {
		int idx = 0;
		if (inData != null && inData.indexOf('.') == -1) {
			return inData;
		} else if ((inData != null && inData.indexOf('.') >= 0)) {
			idx = inData.indexOf('.');
			while (idx >= 0) {
				inData = inData.substring(0, idx) + inData.substring(idx + 1);
				idx = inData.indexOf('.');
			}
		}
		return (inData);
	}

	public static String formatDate(Calendar date) {
		if (date != null)
			return formatDate(date.getTime(), getDateFormat());
		else
			return "";
	}

	public static String formatDate(Calendar date, String sDateFormat) {
		return formatDate(date.getTime(), sDateFormat);
	}

	public static String formatDate(Date date) {
		return formatDate(date, getDateFormat());
	}

	/*
	 * ho tro them format 'Q/yyyy' @author Thanhnx @param date - ngay can lay
	 * format, sDateFormat - format mark @return String - theo format truyen vao
	 * 
	 * @since 04/05/2003
	 */

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

	/**
	 * them kiem tra kieu du lieu Long va Double
	 */
	public static String assignSafeStringFromObject(Object o) {
		if (o != null) {
			if (o instanceof Date)
				return formatDate((Date) o);
			if (o instanceof Calendar)
				return formatDate((Calendar) o);
			if (o instanceof Long)
				return formatLong(((Long) o).longValue());
			if (o instanceof Double)
				return doubleToString(((Double) o).doubleValue());
			else
				return o.toString();
		} else {
			return "";
		}
	}

	public static String convertToLine(Object o) {
		String tmp = assignSafeStringFromObject(o);
		String rs = "";
		char ch[] = tmp.toCharArray();

		for (int i = 0; i < ch.length; i++) {
			if (ch[i] != 10) // ma \n
				rs += ch[i];
		}

		return rs;
	}

	public static String formatBoolean(Boolean inData) {
		if (inData.booleanValue())
			return "Y";
		else
			return "";
	}

	public static String formatBoolean(boolean inData) {
		if (inData)
			return "Y";
		else
			return "";
	}

	/**
	 * 
	 * Thu tuc chuyen gia tri cua bien Double thanh xau co dinh dang #,##0% Su dung
	 * khi cac trang JSP lay du lieu kieu Double tu USOM va hien thi len theo dinh
	 * dang
	 * 
	 * @author cuongnd
	 * @since 28/03/2003
	 * @param Double inData gia tri can dinh dang
	 * @return String xau hien thi gia tri Double theo dinh dang
	 * 
	 */
	public static String formatDouble(double inData) {
		// Locale[] locales = NumberFormat.getAvailableLocales();
		NumberFormat form;
		String valueReturn = "";
		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.GERMAN);
		valueReturn = form.format(new Double(inData));

		return valueReturn;
	}

	public static String formatDouble(Double inData) {
		// Xu ly voi tien am
		boolean isNegative = false;
		// if (inData < 0) {
		// inData = (Double) (inData * -1);
		// isNegative = true;
		// }
		if (inData == null) {
			return "";
		} else {
			if (isNegative)
				return "-" + formatDouble(inData.doubleValue());
			else
				return formatDouble(inData.doubleValue());
		}

	}

	/**
	 * 
	 * Thu tuc chuyen gia tri cua bien Long thanh xau co dinh dang #,##0% Su dung
	 * khi cac trang JSP lay du lieu kieu Long tu USOM va hien thi len theo dinh
	 * dang
	 * 
	 * @author cuongnd
	 * @since 28/03/2003
	 * @param Long inData gia tri can dinh dang
	 * @return String xau hien thi gia tri Long theo dinh dang
	 * 
	 */
	public static String formatInt(int inValue) {
		// Locale[] locales = NumberFormat.getAvailableLocales();
		NumberFormat form;
		String valueReturn = "";
		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.GERMAN);
		valueReturn = form.format(new Integer(inValue));
		return valueReturn;
	}

	public static String formatInt(Integer inValue) {
		if (inValue == null) {
			return "";
		} else {
			return formatLong(inValue.intValue());
		}
	}

	public static String formatLong(long inValue) {
		// Locale[] locales = NumberFormat.getAvailableLocales();
		NumberFormat form;
		String valueReturn = "";
		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.GERMAN);
		valueReturn = form.format(new Long(inValue));
		return valueReturn;
	}

	public static String formatLong(Long inValue) {
		// Xu ly voi tien am
		boolean isNegative = false;
		// if (inValue < 0) {
		// inValue = inValue * -1;
		// isNegative = true;
		// }
		if (inValue == null) {
			return "";
		} else {
			if (isNegative)
				return "-" + formatLong(inValue.longValue());
			else
				return formatLong(inValue.longValue());
		}
	}

	/**
	 * Chuyen so double bieu dien so tien sang dang xau co dau cham phan cach giua
	 * 
	 * @author HUNGHX
	 * @param double - So tien dang so
	 * @return String - So tien dang xau
	 * 
	 */
	public static String formatCurrency_Native(String inValue) {
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
			outValue = "." + inValue.substring(inValue.length() - 3, inValue.length()) + outValue;
			inValue = inValue.substring(0, inValue.length() - 3);
		}
		if (inValue.equals("") || inValue.equals("-")) {
			outValue = inValue + outValue.substring(1);
		} else {
			outValue = inValue + outValue;
		}
		return outValue;
	}

	public static String formatCurrency(String inValue) {
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
			outValue = "," + inValue.substring(inValue.length() - 3, inValue.length()) + outValue;
			inValue = inValue.substring(0, inValue.length() - 3);
		}
		if (inValue.equals("") || inValue.equals("-")) {
			outValue = inValue + outValue.substring(1);
		} else {
			outValue = inValue + outValue;
		}
		return outValue;
	}

	/**
	 * Chuyen so double bieu dien so tien sang dang xau
	 * 
	 * @author ThanhNH7
	 * @param double - So tien dang so
	 * @return String - So tien dang xau
	 * 
	 */
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

	/**
	 * Chuyen so double bieu dien so tien sang dang xau
	 * 
	 * @author ThanhNH7
	 * @param double - So tien dang so
	 * @return String - So tien dang xau
	 * 
	 */
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

	/**
	 * Chuyen so double bieu dien so tien sang dang xau
	 * 
	 * @author ThanhNH7
	 * @param double - So tien dang so
	 * @return String - So tien dang xau
	 * 
	 */
	public static String doubleToString_Native(double dSo_tien) {
		// lam tron ve 2 so thap phan
		double dSotienRound = Math.round(dSo_tien * 100.0) / 100.0;

		String sSo_tien = String.valueOf(dSotienRound);
		String sGia_tri_tra_ve = "";
		int iEPos = sSo_tien.indexOf("E");
		int iCommaPos = sSo_tien.indexOf(".");
		int iSo_mu = 0;
		if (dSotienRound == 0.0D)
			return "0";
		if (iEPos <= 0) {
			if (sSo_tien.indexOf(".") > 0)
				sGia_tri_tra_ve = sSo_tien;
			else
				sGia_tri_tra_ve = sSo_tien;
			sGia_tri_tra_ve = formatCurrency_Native(sGia_tri_tra_ve);
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
		sGia_tri_tra_ve = formatCurrency_Native(sGia_tri_tra_ve);
		return sGia_tri_tra_ve;
	}
//	
//	/**
//	 * Chuyen so dan String bieu dien so tien sang dang Double
//	 * 
//	 * @author ThanhNH7
//	 * @param string -
//	 *            So tien dang String
//	 * @return double - So tien dang so
//	 *  
//	 */
//	public static double stringToDouble(String strSoTien) {
//		String strSoTien_Format = "";
//		if (strSoTien.trim().equals("") == false || strSoTien != null) {
//			strSoTien_Format = strSoTien.replaceAll(" ", "");
//			return Double.parseDouble(strSoTien_Format);
//		} else {
//			return Double.parseDouble("0.0");
//		}
//	}

	public static String formatObject(Object object) {

		if (object instanceof Date)
			return formatDate((Date) object);
		else if (object instanceof Calendar)
			return formatDate((Calendar) object);
		else
			return object.toString();
	}

	public static String convertLongDateToLong(String longDateString) {
		String strYear = longDateString.substring(6);
		String strMonth = longDateString.substring(3, 5);
		String strDay = longDateString.substring(0, 2);

		return strYear + strMonth + strDay;
	}

	// Datqa ham convert tu Long sang String_Date tren form

	public static String convertLongToStringDate(String longDateString) {
		String strYear = longDateString.substring(0, 4);
		String strMonth = longDateString.substring(4, 6);
		String strDay = longDateString.substring(6);

		return strDay + "/" + strMonth + "/" + strYear;
	}

	/**
	 * 
	 * 
	 * Su dung convert tu dinh dang ngay thang ra xau va nguoc lai
	 * 
	 * @author ThanhNH7
	 * @since 23/7/2003
	 * @param String
	 * @return String
	 * 
	 */
	/*
	 * athor : THANHNH7 Ham convert tu dang thoi gian day du dd/mm/yyyy sang dang
	 * yyyymmdd VD : 20/12/1999 => 19991220
	 */
	public static String convertFromDateFormatToString_DaoNguoc(String strDate) {
		String strPerfect = "";
		strDate = strDate.replaceAll("/", "");
		strPerfect = strDate.substring(4, 8) + strDate.substring(2, 4) + strDate.substring(0, 2);
		return strPerfect;
	}

	/*
	 * author : THANHNH7 Ham convert tu dang thoi gian day du dd/mm/yyyy sang dang
	 * ddmmyyyy VD : 20/12/1999 => 20121999
	 */
	public static String convertFromDateFormatToString(String strDate) {
		String strPerfect = "";
		strPerfect = strDate.replaceAll("/", "");
		return strPerfect;
	}

	/*
	 * author : THANHNH7 Ham convert tu dang thoi gian day du yyyymmdd sang dang
	 * dd/mm/yyyy VD : 19991220 => 20/12/1999
	 */
	public static String convertFromStringDNToDateFormat(String strString) {
		String strPerfect = "";
		strPerfect = strString.substring(6, 8) + "/" + strString.substring(4, 6) + "/" + strString.substring(0, 4);
		return strPerfect;
	}

	public static String TimDateOperator(int dd, int mm, int yyyy, String operator, int value) {
		int[] dayOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		GregorianCalendar calendar = new GregorianCalendar();
		if (calendar.isLeapYear(yyyy))
			dayOfMonth[1] = 29;
		int day = dd;
		mm--;
		while (mm > 0) {
			day += dayOfMonth[mm - 1];
			mm--;
		}
		if (operator.compareTo("+") == 0)
			day += value;
		else if (operator.compareTo("-") == 0)
			day -= value;
		if (day < 0) {
			yyyy--;
			if (calendar.isLeapYear(yyyy))
				day += 366;
			else
				day += 365;
		}
		mm = 1;
		while (day > dayOfMonth[mm - 1]) {
			day -= dayOfMonth[mm - 1];
			mm++;
		}
		dd = day;
		String result = dd + "/" + mm + "/" + yyyy;
		String[] temp_date;
		temp_date = result.split("/");
		if (temp_date[0].length() == 1)
			temp_date[0] = "0" + temp_date[0];
		if (temp_date[1].length() == 1)
			temp_date[1] = "0" + temp_date[1];
		result = temp_date[0] + "/" + temp_date[1] + "/" + temp_date[2];
		return result;
	}

	/**
	 * 
	 * @param inData
	 * @return Mo ta: Convert du lieu tu dinh dang ### ### to #######
	 */
	public static String ConvertStringToNumber(String inData) {
		String returnValue = "";
		String[] splited;
		int n = 0;
		if (inData.equals("")) {
			returnValue = "0";
			return returnValue;
		}
		if (inData.equals("0")) {
			returnValue = "0";
			return returnValue;
		}
		if (inData.equals("0.0")) {
			returnValue = "0";
			return returnValue;
		}
		splited = inData.split(" ");
		n = splited.length;
		if (n == 1)
			returnValue = inData;
		if (n > 1) {
			for (int i = 0; i < n; i++) {
				returnValue += splited[i];
			}
		}
		return returnValue;
	}

	/****************************************************
	 * Author: DungTV10
	 * 
	 * @param dateStringInOriginalFormat dateStringInOriginalFormat: String co dang
	 *                                   20020115
	 * @return String co dang 15/01/2002
	 ****************************************************/
	public static String ConvertStringDNtoDateFormat(String dateStringInOriginalFormat) {
		SimpleDateFormat originalFormatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy");

		ParsePosition pos = new ParsePosition(0);
		Date dateFromString = originalFormatter.parse(dateStringInOriginalFormat, pos);
		String dateStringInNewFormat = newFormatter.format(dateFromString);
		return dateStringInNewFormat;
	}

	/****************************************************
	 * Author: DungTV10
	 * 
	 * @param dateStringInOriginalFormat dateStringInOriginalFormat: String co dang
	 *                                   15/01/2001
	 * @return String co dang 20010115
	 ****************************************************/
	public static String ConvertDateFormattoStringDN(String dateStringInOriginalFormat) {
		SimpleDateFormat originalFormatter = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat newFormatter = new SimpleDateFormat("yyyyMMdd");

		ParsePosition pos = new ParsePosition(0);
		Date dateFromString = originalFormatter.parse(dateStringInOriginalFormat, pos);
		String dateStringInNewFormat = newFormatter.format(dateFromString);
		return dateStringInNewFormat;
	}

	public static boolean isNumber(String s) {
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String formatNumber(String input) throws Exception {

		String result = "";
		String InputTemp = "";

		String inputLong = formatTongTien(Double.parseDouble(input));
		try {
			InputTemp = inputLong.substring(0, inputLong.indexOf("."));
		} catch (Exception ex) {
			InputTemp = inputLong;
		}
		if (InputTemp == null)
			return "0";
		else if (InputTemp.length() == 0)
			return "0";
		else if (InputTemp.length() == 1)
			return InputTemp;
		else if (InputTemp.length() == 2)
			return InputTemp;
		else if (InputTemp.length() == 3)
			return InputTemp;
		else {
			String decimal = InputTemp.substring(0, InputTemp.length() - 2);
			String addDot = decimal + '.' + InputTemp.substring(InputTemp.length() - 2, InputTemp.length());
			long decRound = Math.round(Double.parseDouble(addDot));
			result = String.valueOf(decRound) + "00";
		}
		return result;
	}

	public static String formatTongTien(double inData) {
		// Locale[] locales = NumberFormat.getAvailableLocales();
		NumberFormat form;
		String valueReturn = "";
		// Datqa doi lai dinh dang cho so dang #.###.###,##
		form = NumberFormat.getInstance(Locale.JAPAN);

		valueReturn = form.format(new Double(inData));

		return valueReturn.replaceAll(",", "");
	}

	public static String formatTaiKhoan(String input) {
		int index = input.indexOf(".");
		String remain = "";
		String result = "";
		if (index > 0) {
			while (index > 0) {
				String part1 = input.substring(0, index);
				String temp = input.substring(index + 1, input.length());
				index = temp.indexOf(".");
				remain = temp;
				input = temp;
				result += part1;
			}
			result += remain;
			return result;
		} else {
			return input;
		}
	}

	/****************************************************
	 * Author: HuyTQ6
	 * 
	 * @param originDate     ngay goc, string
	 * @param fromDateFormat dinh dang cua origin Date
	 * @param toDateFormat   dinh dang date se convert
	 * @return String co dang toDateFormat
	 ****************************************************/
	public static String convertDate(String originDate, String fromDateFormat, String toDateFormat) {
		SimpleDateFormat originalFormatter = new SimpleDateFormat(fromDateFormat);
		SimpleDateFormat newFormatter = new SimpleDateFormat(toDateFormat);

		ParsePosition pos = new ParsePosition(0);
		Date dateFromString = originalFormatter.parse(originDate, pos);
		String result = newFormatter.format(dateFromString);
		return result;
	}

	/****************************************************
	 * Author: HuyTQ6
	 * 
	 * @param originDate ngay goc, string
	 * @return String co dang toDateFormat
	 ****************************************************/
	public static String convertSqlDateToDateFormat(String originDate) {
		return convertDate(originDate, "yyyy-M-d", "dd/MM/yyyy");
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

	public static String doubleToStringWithoutDecimal(double d) {
		DecimalFormatSymbols symbol = new DecimalFormatSymbols();
		symbol.setGroupingSeparator(' ');
		symbol.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#,###", symbol);
		return format.format(d);
	}
	
	public static String doubleToStringWithoutDecimal(String d){
		DecimalFormatSymbols symbol = new DecimalFormatSymbols();
		symbol.setGroupingSeparator(' ');
		symbol.setDecimalSeparator('.');
		DecimalFormat format = new DecimalFormat("#,###",symbol);
		return format.format(org.apache.commons.lang3.math.NumberUtils.toDouble(
							 	org.apache.commons.lang3.StringUtils.replace(d, " ", ""))
							);
	}
	 public static String ConvertDatetoString(Date dateHQ) throws Exception {
		    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		    String strDate1 = format.format(dateHQ);
		    String strDate2 = strDate1.replaceAll("T", " ");
		    return strDate2;
		   }
}