package com.taskboard.main;

import java.util.ArrayList;

public class EditParameterParser implements ParameterParser {
	
	public EditParameterParser() {
		
	}
	
	public ArrayList<Parameter> parseParameters(String commandString) {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		// remove the commandType token (add, edit, delete, etc.) and remove trailing whitespaces
		String parameterString = commandString.substring(commandString.indexOf(" ")).trim();
		
		ArrayList<DelimiterType> delimiterTypes = extractDelimiterTypes(parameterString);
		if (delimiterTypes.isEmpty()) {
			parameters.add(new Parameter(ParameterType.NAME, parameterString));
		} else {
			int expectedDelimiterId = 0;
			DelimiterType expectedDelimiterType = delimiterTypes.get(expectedDelimiterId);
			String expectedDelimiterName = expectedDelimiterType.name().toLowerCase();
			
			String temporaryString = new String();
			String[] tokens = parameterString.split(" ");
			for (int i = tokens.length - 1; i >= 0; i--) {
				if (tokens[i].toLowerCase().equals(expectedDelimiterName)) {
					if (temporaryString.isEmpty()) {
						// throw exception here (empty parameter exception)
					} else {
						parameters.addAll(convertToParameters(temporaryString, expectedDelimiterType));
						temporaryString = new String();
					}
					
					expectedDelimiterId++;
					if (expectedDelimiterId < delimiterTypes.size()) {
						expectedDelimiterType = delimiterTypes.get(expectedDelimiterId);
						expectedDelimiterName = expectedDelimiterType.name().toLowerCase();
					} else {
						expectedDelimiterType = null;
						expectedDelimiterName = new String(); // name does not have delimiter
					}
				} else {
					temporaryString += tokens[i] + ' ';
				}
			}
			temporaryString = reverseTokens(temporaryString);
			parameters.add(new Parameter(ParameterType.NAME, temporaryString));
		}
		
		return parameters;
	}
	
	private static ArrayList<DelimiterType> extractDelimiterTypes(String parameterString) {
		ArrayList<DelimiterType> delimiterTypes = new ArrayList<DelimiterType>();
		
		parameterString = parameterString.toLowerCase();
		String[] tokens = parameterString.split(" ");
		for (int i = tokens.length - 1; i >= 0; i--) {
			if (tokens[i].equals("from") && !delimiterTypes.contains(DelimiterType.FROM)) {
				delimiterTypes.add(DelimiterType.FROM);
			} else if (tokens[i].equals("to") && !delimiterTypes.contains(DelimiterType.TO)) {
				delimiterTypes.add(DelimiterType.TO);
			} else if (tokens[i].equals("by") && !delimiterTypes.contains(DelimiterType.BY)) {
				delimiterTypes.add(DelimiterType.BY);
			} else if (tokens[i].equals("every") && !delimiterTypes.contains(DelimiterType.EVERY)) {
				delimiterTypes.add(DelimiterType.EVERY);
			}
		}
		
		return delimiterTypes;
	}
	
	private static String reverseTokens(String parameterString) {
		String resultString = new String();
		
		String[] tokens = parameterString.split(" ");
		for (int i = tokens.length - 1; i >= 0; i--) {
			resultString += tokens[i] + ' ';
		}
		
		return resultString.trim();
	}
	
	private static ArrayList<Parameter> convertToParameters(String parameterString, DelimiterType delimiterType) {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		FormatValidator dateFormatValidator = new DateFormatValidator();
		FormatValidator timeFormatValidator = new TimeFormatValidator();
		
		switch (delimiterType) {
			case FROM:
				for (String parameterToken : parameterString.split(" ")) {
					if (dateFormatValidator.isValidFormat(parameterToken)) {
						String startDate = dateFormatValidator.toDefaultFormat(parameterToken);
						parameters.add(new Parameter(ParameterType.START_DATE, startDate));
					} else if (timeFormatValidator.isValidFormat(parameterToken)) {
						String startTime = timeFormatValidator.toDefaultFormat(parameterToken);
						parameters.add(new Parameter(ParameterType.START_TIME, startTime));
					}
				}
				break;
			case TO:
				for (String parameterToken : parameterString.split(" ")) {
					if (dateFormatValidator.isValidFormat(parameterToken)) {
						String endDate = dateFormatValidator.toDefaultFormat(parameterToken);
						parameters.add(new Parameter(ParameterType.END_DATE, endDate));
					} else if (timeFormatValidator.isValidFormat(parameterToken)) {
						String endTime = timeFormatValidator.toDefaultFormat(parameterToken);
						parameters.add(new Parameter(ParameterType.END_TIME, endTime));
					}
				}
				break;
			case BY:
				for (String parameterToken : parameterString.split(" ")) {
					if (dateFormatValidator.isValidFormat(parameterToken)) {
						String date = dateFormatValidator.toDefaultFormat(parameterToken);
						parameters.add(new Parameter(ParameterType.DATE, date));
					} else if (timeFormatValidator.isValidFormat(parameterToken)) {
						String time = timeFormatValidator.toDefaultFormat(parameterToken);
						parameters.add(new Parameter(ParameterType.TIME, time));
					}
				}
				break;
			case EVERY:
				// TBD: recurring task
				break;
			default:
				break;
		}
		
		return parameters;
	}
	
}