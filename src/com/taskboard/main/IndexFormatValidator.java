package com.taskboard.main;

public class IndexFormatValidator implements FormatValidator {
	
	public IndexFormatValidator() {
		
	}
	
	public boolean isValidFormat(String token) {
		token = token.toLowerCase();
		if (isNumeric(token)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isNumeric(String token) {
		try {
			Integer.parseInt(token);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public String toDefaultFormat(String token) {
		return token;
	}
	
}
