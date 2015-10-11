package com.taskboard.main;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatValidator implements FormatValidator {
	
	// constants
	
	private static final int MILLISECONDS_PER_DAY = 86400000;
	private static final int DAY_INDEX_MONDAY = 1;
	private static final int DAY_INDEX_TUESDAY = 2;
	private static final int DAY_INDEX_WEDNESDAY = 3;
	private static final int DAY_INDEX_THURSDAY = 4;
	private static final int DAY_INDEX_FRIDAY = 5;
	private static final int DAY_INDEX_SATURDAY = 6;
	private static final int DAY_INDEX_SUNDAY = 7;
	
	// constructors
	
	public DateFormatValidator() {
		
	}
	
	// functionalities
	
	public boolean isValidFormat(String token) {
		token = token.toLowerCase();
		switch (token) {
			case "today":
			case "tomorrow":
			case "monday":
			case "tuesday":
			case "wednesday":
			case "thursday":
			case "friday":
			case "saturday":
			case "sunday":
				return true;
			default:
				if (token.length() == 10 && token.indexOf('/') == 2 && token.lastIndexOf('/') == 5) {
					return true;
				} else {
					return false;
				}
		}
	}
	
	public String toDefaultFormat(String token) {
		SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		
		token = token.toLowerCase();
		switch (token) {
			case "today":
				return defaultDateFormat.format(today);
			case "tomorrow":
				Date tomorrow = new Date(today.getTime() + MILLISECONDS_PER_DAY);
				return defaultDateFormat.format(tomorrow);
			case "mon":
			case "monday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_MONDAY));
			case "tue":
			case "tuesday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_TUESDAY));
			case "wed":
			case "wednesday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_WEDNESDAY));
			case "thu":
			case "thursday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_THURSDAY));
			case "fri":
			case "friday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_FRIDAY));
			case "sat":
			case "saturday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_SATURDAY));
			case "sun":
			case "sunday":
				return defaultDateFormat.format(getNextOccurenceDate(DAY_INDEX_SUNDAY));
			default:
				if (token.length() == 10 && token.indexOf('/') == 2 && token.lastIndexOf('/') == 5) {
					return token;
				} else {
					return null;
				}
		}
	}
	
	private static Date getNextOccurenceDate(int dayIndex) {
		SimpleDateFormat dayIndexFormat = new SimpleDateFormat("u");
		Date today = new Date();
		int todayDayIndex = Integer.parseInt(dayIndexFormat.format(today));
		
		if (todayDayIndex < dayIndex) {
			Date monday = new Date(today.getTime() + MILLISECONDS_PER_DAY * 
						  (dayIndex - todayDayIndex));
			return monday;
		} else {
			Date monday = new Date(today.getTime() + MILLISECONDS_PER_DAY * 
					  	  (dayIndex - todayDayIndex + 7));
			return monday;
		}
	}
	
}
