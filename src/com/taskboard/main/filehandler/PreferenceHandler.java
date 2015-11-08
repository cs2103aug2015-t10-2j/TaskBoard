//@@author A0129889A 
package com.taskboard.main.filehandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.taskboard.main.GlobalLogger;
import com.taskboard.main.userinterface.UserInterface;

public class PreferenceHandler {

	private static final String MESSAGE_ERROR_FOR_CREATING_EXISTNG_FILE = "The file already exists.";
	private static final String MESSAGE_ERROR_FOR_OPENING_NON_EXISTING_FILE = "The file does not exists.";

	private File _preferenceFile;

	private static Logger _logger = GlobalLogger.getInstance().getLogger();

	public PreferenceHandler() {
	}
	
	public ArrayList<String> createNewFile(String fileName) throws IllegalArgumentException, IOException {
		String newFileName = fileName + ".pref";
		_preferenceFile = new File(newFileName);
		boolean doesFileExist = doesFileExist(_preferenceFile);

		ArrayList<String> contents;

		// assert doesFileExist: false;
		if (!doesFileExist) {
			_preferenceFile.createNewFile();
			contents = new ArrayList<String>();
			contents.add(UserInterface.getInstance().getDefaultBackgroundFilePath());
			contents.add(Integer.toString(UserInterface.getInstance().getDefaultReminderHour()));
			updateTempPreferenceToFile(contents);
		} else {
			throw new IllegalArgumentException(MESSAGE_ERROR_FOR_CREATING_EXISTNG_FILE);
		}
		_logger.log(Level.INFO, "Create a new file.");
		return contents;
	}

	public ArrayList<String> openExistingFile(String fileName) throws IllegalArgumentException, FileNotFoundException {
		String newFileName = fileName + ".pref";
		_preferenceFile = new File(newFileName);
		boolean doesFileExist = doesFileExist(_preferenceFile);

		ArrayList<String> contents;

		if (doesFileExist) {
			Scanner scanFileToCopy = new Scanner(_preferenceFile);
			contents = copyExistingContentsFromFile(scanFileToCopy);
			if (contents.isEmpty()) {
				contents.add(UserInterface.getInstance().getDefaultBackgroundFilePath());
				contents.add(Integer.toString(UserInterface.getInstance().getDefaultReminderHour()));
				try {
					updateTempPreferenceToFile(contents);
				} catch (IOException e) {
					_logger.log(Level.SEVERE, "Reopening existing file, exception should not occur");
					assert false: "Reopening existing file, exception should not occur.";
				}
			}
			scanFileToCopy.close();
		} else {
			throw new IllegalArgumentException(MESSAGE_ERROR_FOR_OPENING_NON_EXISTING_FILE);
		}
		_logger.log(Level.INFO, "Open an existing file.");
		return contents;
	}

	private boolean doesFileExist(File fileToCheck) {
		if (fileToCheck.exists()) {
			return true;
		}
		_logger.log(Level.INFO, "Check whether if a file already exists.");
		return false;
	}

	private ArrayList<String> copyExistingContentsFromFile(Scanner scanFileToCopy) {
		ArrayList<String> contents = new ArrayList<String>();

		String content = new String();

		while (scanFileToCopy.hasNext()) {
			content = scanFileToCopy.nextLine();
			contents.add(content);

//			if (formattedDetail.contains(MARKER_FOR_NEXT_ENTRY_IN_FILE)) {
//				entries.add(entry);
//				entry = new Entry();
//			}
//
//			if (!formattedDetail.isEmpty()) {
//				String[] splitFormattedDetail = formattedDetail.split(": ");
//				String detailType = splitFormattedDetail[INDEX_OF_DETAIL_TYPE].trim();
//				String detail = splitFormattedDetail[INDEX_OF_DETAIL].trim();
//
//				Parameter parameter = new Parameter();
//				parameter.setParameterType(ParameterType.valueOf(detailType));
//				parameter.setParameterValue(detail);
//				entry.addToParameters(parameter);
//			}
		}
//
//		if (!entries.isEmpty()) {
//			// to add the final entry once end of file is reached
//			entries.add(entry);
//			entries.remove(INDEX_OF_EMPTY_ENTRY);
//		}
		_logger.log(Level.INFO, "Copy data from file to temp storage.");
		return contents;
	}
	
	private void copyAllEntriesToFile(FileWriter fileToAdd, ArrayList<String> contents) throws IOException {
		for (int i = 0; i < contents.size(); i++) {
			addSingleStringToFile(fileToAdd, contents.get(i));
		}
	}

	private void addSingleStringToFile(FileWriter fileToAdd, String content) throws IOException {
		fileToAdd.write(content);
		fileToAdd.write(System.lineSeparator());
		fileToAdd.flush();
	}
	
	public void updateTempPreferenceToFile(ArrayList<String> contents) throws IOException {
		FileWriter fileToAdd = new FileWriter(_preferenceFile);
		copyAllEntriesToFile(fileToAdd, contents);
		fileToAdd.close();
		_logger.log(Level.INFO, "Copy data from temp storage to file.");
	}
}
