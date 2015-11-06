//@@author A0123935E
package com.taskboard.main.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.taskboard.main.GlobalLogger;
import com.taskboard.main.IndexProcessor;
import com.taskboard.main.SelectiveFilterProcessor;
import com.taskboard.main.TempStorageManipulator;
import com.taskboard.main.util.Entry;
import com.taskboard.main.util.Parameter;
import com.taskboard.main.util.Response;

public class DeleteCommand extends Command {
	
	private static final String MESSAGE_AFTER_DELETE = "Entry successfully deleted:";
	private static final String MESSAGE_ERROR_FOR_DELETE = "The deletion was unsuccessful.";
	
	public DeleteCommand(ArrayList<Parameter> parameters) {
		assert parameters != null;
		_parameters = parameters;
		
		if (getTempStorageManipulator() == null) {
			_tempStorageManipulator = new TempStorageManipulator();
		}
		
		_logger = GlobalLogger.getInstance().getLogger();
	}
	
	public Response executeCommand() {
		assert _parameters.size() > 0;
		_logger.log(Level.INFO, "Commenced execution of DeleteCommand");
		
		ArrayList<Entry> initialTempStorage = new ArrayList<Entry>();
		for (Entry entry: _tempStorageManipulator.getTempStorage()) {
			initialTempStorage.add(new Entry(entry));
		}
		
		ArrayList<Entry> initialTempArchive = new ArrayList<Entry>();
		for (Entry entry: _tempStorageManipulator.getTempArchive()) {
			initialTempArchive.add(new Entry(entry));
		}
		
		Response responseForDelete = new Response();
		if (isDeleteByIndex()) {
			IndexProcessor indexProcessorForDelete = new IndexProcessor();
			ArrayList<Entry> entries = _tempStorageManipulator.getTempStorage();
			responseForDelete = indexProcessorForDelete.processInputIndex(_parameters, entries);
			if (!responseForDelete.isSuccess()) {
				return responseForDelete;
			}
			
			_logger.log(Level.INFO, "Start processing delete by index");
			responseForDelete = processDeleteByIndex();
		} else {
			_logger.log(Level.INFO, "Start processing delete by filtering");
			responseForDelete = processDeleteByFiltering();
		}
		
		if (responseForDelete.isSuccess()) {
			_tempStorageManipulator.setLastTempStorage(initialTempStorage);
			_tempStorageManipulator.setLastTempArchive(initialTempArchive);
		}
		
		return responseForDelete;
	}
	
	private boolean isDeleteByIndex() {
		Parameter indexParameter = getIndexParameter();
		if (indexParameter != null) {
			return true;
		}
		
		return false; 
	}
		
	private Response processDeleteByIndex() {
		String index = getDetailFromParameter(getIndexParameter());
		int indexValue = Integer.parseInt(index);
		int tempStorageIndex = indexValue - 1;
		Response responseForDeleteByIndex = new Response();
		try {
			Entry entryToDelete = _tempStorageManipulator.getTempStorage().get(tempStorageIndex);
			_tempStorageManipulator.deleteFromTempStorage(tempStorageIndex);
			setSuccessResponseForDeleteByIndex(responseForDeleteByIndex, entryToDelete);
			_logger.log(Level.INFO, "Generated success response for delete by index");
		} catch (IOException ex) {
			setFailureResponseForDelete(responseForDeleteByIndex);
			_logger.log(Level.INFO, "Generated failure response for delete by index");
		}
		
		return responseForDeleteByIndex;
	}
	
	private void setSuccessResponseForDeleteByIndex(Response response, Entry entry) {
		response.setIsSuccess(true);
		String userFeedback = getFeedbackForSuccessfulDeleteByIndex(entry);
		response.setFeedback(userFeedback);
		response.setEntries(_tempStorageManipulator.getTempStorage());
	}
	
	private String getFeedbackForSuccessfulDeleteByIndex(Entry entry) {
		String feedback = MESSAGE_AFTER_DELETE.concat("<br>").concat("<br>").concat(entry.toHTMLString());
		
		return feedback;
	}
	
	private void setFailureResponseForDelete(Response response) {
		response.setIsSuccess(false);
		response.setFeedback(MESSAGE_ERROR_FOR_DELETE);
	}
	
	private Response processDeleteByFiltering() {
		SelectiveFilterProcessor selectiveFilterProcessor = new SelectiveFilterProcessor();
		ArrayList<Entry> entries = _tempStorageManipulator.getTempStorage();
		Response responseForFiltering = selectiveFilterProcessor.processFiltering(entries, _parameters);
		ArrayList<Entry> filteredEntries = selectiveFilterProcessor.getFilteredEntries();
		try {
			if (!filteredEntries.isEmpty()) {
				_tempStorageManipulator.deleteFromTempStorage(filteredEntries);
			}
			
			setSuccessResponseForDeleteByFiltering(responseForFiltering, filteredEntries);
			_logger.log(Level.INFO, "Generated success response for delete by filtering");
			
			return responseForFiltering;
		} catch (IOException ex) {
			Response failedResponseForDelete = new Response();
			setFailureResponseForDelete(failedResponseForDelete);
			_logger.log(Level.INFO, "Generated failure response for delete by filtering");
			
			return failedResponseForDelete;
		}
	}
	
	private void setSuccessResponseForDeleteByFiltering(Response response, ArrayList<Entry> filteredEntries) {
		String userFeedback = response.getFeedback().replace("found", "deleted");
		userFeedback = userFeedback.replace(".", ":");
		userFeedback = userFeedback.concat("<br>");
		for (int i = 0; i < filteredEntries.size(); i++) {
			Entry entry = filteredEntries.get(i);
			userFeedback = userFeedback.concat("<br>").concat(entry.toHTMLString());
		}
		response.setFeedback(userFeedback);
		response.setEntries(_tempStorageManipulator.getTempStorage());
	}
}