package com.pht.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.springframework.lang.Nullable;

import com.pht.common.constants.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeUtils {

	private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter
			.ofPattern(CommonConstants.DateTimePattern.FORMAT_24H);
	private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter
			.ofPattern(CommonConstants.DateTimePattern.FORMAT_DATE_MONTH_YEAR);

	public static Date localDateTimeToDate(LocalDateTime localDateTime) {
		return localDateTime != null ? Date.from(localDateTimeToInstant(localDateTime)) : null;
	}

	public static Date localDateToDate(LocalDate localDate) {
		return localDate != null ? Date.from(localDateToInstant(localDate)) : null;
	}

	public static LocalDate strToLocalDate(String dateString, String pattent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattent);
		try {
			return LocalDate.parse(dateString, formatter);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return LocalDate.now();
		}
	}
	


	public static String localDateTimeToStr(LocalDateTime date, String pattent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattent);
		return date.format(formatter);
	}

	public static String localDateToStr(LocalDate date, String pattent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattent);
		return date.format(formatter);
	}

	public static LocalDateTime dateToLocalDateTime(Date date) {
		return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
	}

	public static LocalDate dateToLocalDate(Date date) {
		return date != null ? date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
	}

	public static LocalDateTime milliToLocalDateTime(long time) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
	}

	public static LocalDate milliToLocalDate(long time) {
		return milliToLocalDateTime(time).toLocalDate();
	}

	public static LocalDateTime strToLocalDateTime(String dateString, String pattent) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattent);
		try {
			return LocalDateTime.parse(dateString, formatter);
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return LocalDateTime.now();
		}
	}

	public static String dateToString(LocalDateTime localDateTime, @Nullable String pattern) {
		if (localDateTime != null) {
			DateTimeFormatter formatter = getDateTimeFormatter(pattern);

			try {
				return formatter.format(localDateTime);
			} catch (Exception ex) {
				log.trace(ex.getMessage(), ex);
			}
		}

		return "";
	}

	public static Instant localDateToInstant(LocalDate localDate) {
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
	}

	public static Instant localDateTimeToInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
	}

	public static long localDateToMilli(LocalDate localDate) {
		return localDateToInstant(localDate).toEpochMilli();
	}

	public static long localDateTimeToMilli(LocalDateTime localDateTime) {
		return localDateTimeToInstant(localDateTime).toEpochMilli();
	}

	public static LocalDateTime stringToLocalDateTime(String date, @Nullable String pattern) {
		if (date != null) {
			DateTimeFormatter formatter = getDateTimeFormatter(pattern);

			try {
				return LocalDateTime.parse(date, formatter);
			} catch (Exception ex) {
				log.trace(ex.getMessage(), ex);
			}
		}

		return null;
	}

	public static String dateToString(LocalDate localDate, @Nullable String pattern) {
		if (localDate != null) {
			DateTimeFormatter formatter = getDateFormatter(pattern);

			try {
				return formatter.format(localDate);
			} catch (Exception ex) {
				log.trace(ex.getMessage(), ex);
			}
		}

		return "";
	}

	public static LocalDate stringToLocalDate(String date, @Nullable String pattern) {
		if (date != null) {
			DateTimeFormatter formatter = getDateFormatter(pattern);

			try {
				return LocalDate.parse(date, formatter);
			} catch (Exception ex) {
				log.trace(ex.getMessage(), ex);
			}
		}

		return null;
	}

	public static String dateToString(Date date, @Nullable String pattern) {
		if (date != null) {
			SimpleDateFormat dateFormat = createSimpleDateFormat(pattern);

			try {
				return dateFormat.format(date);
			} catch (Exception ex) {
				log.trace(ex.getMessage(), ex);
			}
		}

		return "";
	}

	public static Date stringToDate(String date, @Nullable String pattern) {
		if (date != null) {
			SimpleDateFormat dateFormat = createSimpleDateFormat(pattern);

			try {
				return dateFormat.parse(date);
			} catch (Exception ex) {
				log.trace(ex.getMessage(), ex);
			}
		}

		return null;
	}

	public static SimpleDateFormat createSimpleDateFormat(@Nullable String pattern) {
		if (!ValidationUtils.isNullOrEmpty(pattern)) {
			return new SimpleDateFormat(pattern);
		} else {
			return new SimpleDateFormat(CommonConstants.DateTimePattern.FORMAT_24H); // default pattern
		}
	}

	public static DateTimeFormatter getDateTimeFormatter(@Nullable String pattern) {
		if (!ValidationUtils.isNullOrEmpty(pattern)) {
			return DateTimeFormatter.ofPattern(pattern);
		} else {
			return DEFAULT_DATETIME_FORMATTER;
		}
	}

	public static DateTimeFormatter getDateFormatter(@Nullable String pattern) {
		if (!ValidationUtils.isNullOrEmpty(pattern)) {
			return DateTimeFormatter.ofPattern(pattern);
		} else {
			return DEFAULT_DATE_FORMATTER;
		}
	}
}