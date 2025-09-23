package com.fis.ws.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.time.DateFormatUtils;

public class DateUtils {

	public static XMLGregorianCalendar convertDate(Date serviceDate) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(serviceDate);
		try {
			XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			//remove time zone 2017-04-18T00:00:00+02:00 -> 2017-04-18T00:00:00
			xmlDate.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
			xmlDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			return xmlDate;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException();
		}
	}
	
	public static Date toDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
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
	
	public static String convertDate(String originDate, String fromDateFormat, String toDateFormat){
		SimpleDateFormat originalFormatter = new SimpleDateFormat (fromDateFormat);
		SimpleDateFormat newFormatter = new SimpleDateFormat (toDateFormat);		
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
	public static Date parseMultipleFormatDate(String strDate) throws ParseException{
		String[] frmDate = new String[]{"yyyy-MM-dd","dd-MM-yyyy","yyyy/MM/dd","dd/MM/yyyy"};
		return org.apache.commons.lang3.time.DateUtils.parseDate(strDate, frmDate);
	}
	
	public static String convertCDDate(String cdDate) {
		try {
			Date tmPDate = org.apache.commons.lang3.time.DateUtils.parseDate(cdDate, new String[] {"yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy HH:mm:ss"});
			cdDate = DateFormatUtils.format(tmPDate, "dd/MM/yyyy HH:mm:ss");			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cdDate;
	}
	
	public static String convertDateToString(java.util.Date date, String datePattern) {
		if (date == null) {
			return null;
		} else {
			final String loc = "DateUtils" + ".dateToString(" + date + "," + datePattern + ")";
			try {
				SimpleDateFormat fmt = new SimpleDateFormat(datePattern);
				fmt.setTimeZone(TimeZone.getTimeZone("GMT+7"));
				return fmt.format(date);
			} catch (Exception e) {

				return "";
			}
		}
	}
}
