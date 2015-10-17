package com.taskboard.main;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

public class AddCommand extends Command {
	
	private static final String MESSAGE_AFTER_ADD = "\"%1$s\" added!";
	private static final String MESSAGE_ERROR_FOR_ADD = "The entry could not be added to the file.";
//	private static final String MESSAGE_ERROR_FOR_NO_DATE = "No date provided.";
//	private static final String MESSAGE_ERROR_FOR_NO_START_DATE = "No start date provided.";
//	private static final String MESSAGE_ERROR_FOR_INVALID_DATE_TIME = "Invalid date time provided.";
//	private static final String MESSAGE_ERROR_FOR_PAST_DATE_TIME = "Past date time provided.";
//	
//	private static final String FORMAT_DEFAULT_TIME = "00:00";
//	private static final String FORMAT_DATE_TIME = "dd/MM/yyyy'T'HH:mm";
	
	public AddCommand(CommandType commandType, ArrayList<Parameter> parameters) {
		_commandType = commandType;
		_parameters = parameters;
		
		if (getTempStorageManipulator() == null) {
			_tempStorageManipulator = new TempStorageManipulator();
		}
	}
	
	public Response executeCommand() {
		Response responseForAdd = new Response();
		
		if (isAddFloatingTask()) {
			responseForAdd = addFloatingTask();
		} else if (isAddDeadlineTask()) {
			responseForAdd = addDeadlineTask();
		} else if (isAddEvent()) {
			responseForAdd = addEvent();
		}
		
		return responseForAdd;
	}
	
	private boolean isAddFloatingTask() {
		for (int i = 0; i < _parameters.size(); i++) {
			Parameter parameter = _parameters.get(i);
			ParameterType parameterType = parameter.getParameterType();
			
			if (parameterType == ParameterType.DATE || parameterType == ParameterType.START_DATE) {
				return false;
			}
		}
		
		return true;
	}
	
	private Response addFloatingTask() {
		Response responseForAddFloating = new Response();
		
		String taskName = "";
		String priority = "";
		String category = "";
		
		for (int i = 0; i < _parameters.size(); i++) {
			Parameter parameter = _parameters.get(i);
			ParameterType parameterType = parameter.getParameterType();
			
			switch (parameterType) {
				case NAME:
					taskName = parameter.getParameterValue();
					break;
				case PRIORITY:
					priority = parameter.getParameterValue();
					break;
				case CATEGORY:
					category = parameter.getParameterValue();
					break;
			}
		}
		
		responseForAddFloating = processFloatingTaskForStorage(taskName, priority, category);
		return responseForAddFloating;
	}
	
	private Response processFloatingTaskForStorage(String taskName, String priority, String category) {
		Response responseForAddFloating = new Response();
		
		Entry floatingTask = formatFloatingTaskParameters(taskName, priority, category);
		
		try {
			_tempStorageManipulator.addToTempStorage(floatingTask);
			setSuccessResponseForAdd(responseForAddFloating, taskName);
		} catch (IOException ex) {
			setFailureResponseForAdd(responseForAddFloating);
		}
			
		return responseForAddFloating;
	}
	
	private Entry formatFloatingTaskParameters(String taskName, String priority, String category) {
		Entry floatingTask = new Entry();
		
		Parameter taskNameParameter = new Parameter(ParameterType.NAME, taskName);
		floatingTask.addToParameters(taskNameParameter);
		
		if (!priority.isEmpty()) {
			Parameter priorityParameter = new Parameter(ParameterType.PRIORITY, priority);
			floatingTask.addToParameters(priorityParameter);
		}
		
		if (!category.isEmpty()) {
			Parameter categoryParameter = new Parameter(ParameterType.CATEGORY, category);
			floatingTask.addToParameters(categoryParameter);
		}
		
		return floatingTask;
	}
	
	private void setSuccessResponseForAdd(Response response, String entryName) {
		response.setIsSuccess(true);
		String userFeedback = getFeedbackForUser(MESSAGE_AFTER_ADD, entryName);
		response.setFeedback(userFeedback);
		response.setEntries(_tempStorageManipulator.getTempStorage());
	}
	
	private void setFailureResponseForAdd(Response response) {
		response.setIsSuccess(false);
		IOException exObj = new IOException(MESSAGE_ERROR_FOR_ADD);
		response.setException(exObj);
	}
	
	private boolean isAddDeadlineTask() {
		for (int i = 0; i < _parameters.size(); i++) {
			Parameter parameter = _parameters.get(i);
			ParameterType parameterType = parameter.getParameterType();
			
			if (parameterType == ParameterType.DATE) {
				return true;
			}
		}
		
		return false;
	}
		
	private Response addDeadlineTask() {
		Response responseForAddDeadline = new Response();
		
		String taskName = "";
		String date = "";
		String time = "";
		String priority = "";
		String category = "";
		
		for (int i = 0; i < _parameters.size(); i++) {
			Parameter parameter = _parameters.get(i);
			ParameterType parameterType = parameter.getParameterType();
			
			switch (parameterType) {
				case NAME:
					taskName = parameter.getParameterValue();
					break;
				case DATE:
					date = parameter.getParameterValue();
					break;
				case TIME:
					time = parameter.getParameterValue();
					break;
				case PRIORITY:
					priority = parameter.getParameterValue();
					break;
				case CATEGORY:
					category = parameter.getParameterValue();
					break;
			}
		}
		
		DateTimeValidator dateTimeValidator = new DateTimeValidator();
		Date currentDate = new Date();
		responseForAddDeadline = dateTimeValidator.validateDateTimeDetails(date, time, currentDate);
		
		if (responseForAddDeadline.isSuccess() == true) {
			responseForAddDeadline = processDeadlineTaskForStorage(taskName, date, time, priority, category);
		}
	
		return responseForAddDeadline;
	}
	
//	private Response validateDateTimeDetailsForDeadlineTask(String date, String time) {
//		Response responseForDateTime = new Response();
//		
//		if (date.isEmpty()) {
//			setFailureResponseForNoDate(responseForDateTime);
//			return responseForDateTime;
//		}
//		
//		String dateTime = getDateTimeFormat(date, time);
//		Date inputDate = getInputDate(dateTime);
//		Date todayDate = new Date();
//		responseForDateTime = checkValidityOfInputDate(inputDate, todayDate);
//		
//		return responseForDateTime;
//	}
//	
//	private void setFailureResponseForNoDate(Response response) {
//		response.setIsSuccess(false);
//		IllegalArgumentException exObj = new IllegalArgumentException(MESSAGE_ERROR_FOR_NO_DATE);
//		response.setException(exObj);
//	}
//	
//	private String getDateTimeFormat(String date, String time) {
//		if (time.isEmpty()) {
//			time = FORMAT_DEFAULT_TIME;
//		}
//		
//		String dateTime = date.concat("T").concat(time);
//		
//		return dateTime;
//	}
//	
//	private Date getInputDate(String dateTime) {
//		 try {
//			 DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
//	         dateFormat.setLenient(false);
//	         Date inputDate = dateFormat.parse(dateTime);
// 	         
//	         return inputDate;
//	     } catch (ParseException e) {
//	         return null;
//	     }
//	}
//	
//	private Response checkValidityOfInputDate(Date inputDate, Date referenceDate) {
//		Response responseForInputDate = new Response();
//		
//		if (inputDate == null) {
//			setFailureResponseForInvalidDateTime(responseForInputDate);
//		} else {
//			
//			if (inputDate.after(referenceDate)) {
//				responseForInputDate.setIsSuccess(true);
//			} else {
//				setFailureResponseForPastDateTime(responseForInputDate);
//			}
//		}	
//		
//		return responseForInputDate;
//	}
//	
//	private void setFailureResponseForInvalidDateTime(Response response) {
//		response.setIsSuccess(false);
//		IllegalArgumentException exObj = new IllegalArgumentException(MESSAGE_ERROR_FOR_INVALID_DATE_TIME);
//		response.setException(exObj);
//	}
//	
//	private void setFailureResponseForPastDateTime(Response response) {
//		response.setIsSuccess(false);
//		IllegalArgumentException exObj = new IllegalArgumentException(MESSAGE_ERROR_FOR_PAST_DATE_TIME);
//		response.setException(exObj);
//	}
	
	private Response processDeadlineTaskForStorage(String taskName, String date, String time, String priority, 
			                                       String category) {
		Response responseForAddDeadline = new Response();
		
		Entry deadlineTask = formatDeadlineTaskParameters(taskName, date, time, priority, category);
		
		try {
			_tempStorageManipulator.addToTempStorage(deadlineTask);
			setSuccessResponseForAdd(responseForAddDeadline, taskName);
		} catch (IOException ex) {
			setFailureResponseForAdd(responseForAddDeadline);
		}
		
		return responseForAddDeadline;
	}
	
	private Entry formatDeadlineTaskParameters(String taskName, String date, String time, String priority,
			                                   String category) {
		Entry deadlineTask = new Entry();
		
		Parameter taskNameParameter = new Parameter(ParameterType.NAME, taskName);
		deadlineTask.addToParameters(taskNameParameter);
		
		Parameter dateParameter = new Parameter(ParameterType.DATE, date);
		deadlineTask.addToParameters(dateParameter);
		
		if (!time.isEmpty()) {
			Parameter timeParameter = new Parameter(ParameterType.TIME, time);
			deadlineTask.addToParameters(timeParameter);
		}
		
		if (!priority.isEmpty()) {
			Parameter priorityParameter = new Parameter(ParameterType.PRIORITY, priority);
			deadlineTask.addToParameters(priorityParameter);
		}
		
		if (!category.isEmpty()) {
			Parameter categoryParameter = new Parameter(ParameterType.CATEGORY, category);
			deadlineTask.addToParameters(categoryParameter);
		}
		
		return deadlineTask;
	}
	
	private boolean isAddEvent() {
		for (int i = 0; i < _parameters.size(); i++) {
			Parameter parameter = _parameters.get(i);
			ParameterType parameterType = parameter.getParameterType();
			
			if (parameterType == ParameterType.START_DATE) {
				return true;
			}
		}
		
		return false;
	}
	
	private Response addEvent() {
		Response responseForAddEvent = new Response();
		
		String eventName = "";
		String startDate = "";
		String startTime = "";
		String endDate = "";
		String endTime = "";
		String priority = "";
		String category = "";
		
		for (int i = 0; i < _parameters.size(); i++) {
			Parameter parameter = _parameters.get(i);
			ParameterType parameterType = parameter.getParameterType();
			
			switch (parameterType) {
				case NAME:
					eventName = parameter.getParameterValue();
					break;
				case START_DATE:
					startDate = parameter.getParameterValue();
					break;
				case START_TIME:
					startTime = parameter.getParameterValue();
					break;
				case END_DATE:
					endDate = parameter.getParameterValue();
					break;
				case END_TIME:
					endTime = parameter.getParameterValue();
					break;
				case PRIORITY:
					priority = parameter.getParameterValue();
					break;
				case CATEGORY:
					category = parameter.getParameterValue();
					break;
			}
		}
		
		if (endDate.isEmpty()) {
			endDate = startDate;
		}
		
		DateTimeValidator startDateTimeValidator = new DateTimeValidator();
		Date currentDate = new Date();
		responseForAddEvent = startDateTimeValidator.validateDateTimeDetails(startDate, startTime, currentDate);
		
		if (responseForAddEvent.isSuccess() == true) {
			DateTimeValidator endDateTimeValidator = new DateTimeValidator();
			Date inputStartDate = startDateTimeValidator.getInputDate();
			responseForAddEvent = endDateTimeValidator.validateDateTimeDetails(endDate, endTime, inputStartDate);
		}
		
		if (responseForAddEvent.isSuccess() == true) {
			responseForAddEvent = processEventForStorage(eventName, startDate, startTime, endDate, endTime, 
					                                     priority, category);
		}
		
		return responseForAddEvent;
	}
	
//	private Response validateDateTimeDetailsForEvent(String startDate, String startTime, String endDate, 
//                                                     String endTime) {
//		Response responseForDateTime = new Response();
//		
//		if (startDate.isEmpty()) {
//			setFailureResponseForNoStartDate(responseForDateTime);
//			return responseForDateTime;
//		}
//		
//		String startDateTime = getDateTimeFormat(startDate, startTime);
//		Date inputStartDate = getInputDate(startDateTime);
//		Date todayDate = new Date();
//		responseForDateTime = checkValidityOfInputDate(inputStartDate, todayDate);
//		
//		if (responseForDateTime.isSuccess() == true) {			
//			String endDateTime = getDateTimeFormat(endDate, endTime);
//			Date inputEndDate = getInputDate(endDateTime);
//			responseForDateTime = checkValidityOfInputDate(inputEndDate, inputStartDate);
//		}
//		
//		return responseForDateTime;
//	}
//	
//	private void setFailureResponseForNoStartDate(Response response) {
//		response.setIsSuccess(false);
//		IllegalArgumentException exObj = new IllegalArgumentException(MESSAGE_ERROR_FOR_NO_START_DATE);
//		response.setException(exObj);
//	}
	
	private Response processEventForStorage(String eventName, String startDate, String startTime, String endDate, 
                                            String endTime, String priority, String category) {
		Response responseForEvent = new Response();
		
		Entry event = formatEventParameters(eventName, startDate, startTime, endDate, endTime,
				                            priority, category);
		
		try {
			_tempStorageManipulator.addToTempStorage(event);
			setSuccessResponseForAdd(responseForEvent, eventName);
		} catch (IOException ex) {
			setFailureResponseForAdd(responseForEvent);
		}
		
		return responseForEvent;
	}
	
	private Entry formatEventParameters(String eventName, String startDate, String startTime, String endDate,
                                        String endTime, String priority, String category) {
		Entry event = new Entry();
		
		Parameter eventNameParameter = new Parameter(ParameterType.NAME, eventName);
		event.addToParameters(eventNameParameter);
		
		Parameter startDateParameter = new Parameter(ParameterType.START_DATE, startDate);
		event.addToParameters(startDateParameter);
		
		if (!startTime.isEmpty()) {
			Parameter startTimeParameter = new Parameter(ParameterType.START_TIME, startTime);
			event.addToParameters(startTimeParameter);
		}
		
		Parameter endDateParameter = new Parameter(ParameterType.END_DATE, endDate);
		event.addToParameters(endDateParameter);
		
		if (!endTime.isEmpty()) {
			Parameter endTimeParameter = new Parameter(ParameterType.END_TIME, endTime);
			event.addToParameters(endTimeParameter);
		}
		
		if (!priority.isEmpty()) {
			Parameter priorityParameter = new Parameter(ParameterType.PRIORITY, priority);
			event.addToParameters(priorityParameter);
		}
		
		if (!category.isEmpty()) {
			Parameter categoryParameter = new Parameter(ParameterType.CATEGORY, category);
			event.addToParameters(categoryParameter);
		}
		
		return event;
	}
}
