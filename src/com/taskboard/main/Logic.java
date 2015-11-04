 package com.taskboard.main;

import com.taskboard.main.command.Command;
import com.taskboard.main.parser.CommandParser;

public class Logic {
	
	// attribute
	
	private CommandParser _commandParser;
	
	// constructor
	
	public Logic() {
		_commandParser = new CommandParser();
	}
		
	public Response processCommand(String userInput) {
		Response responseForOperations = new Response();
		
		try {
			Command commandInput = _commandParser.parseCommand(userInput);
			responseForOperations = commandInput.executeCommand();
		} catch (IllegalArgumentException ex) {
			responseForOperations.setIsSuccess(false);
			responseForOperations.setFeedback(ex.getMessage());
		}
		
		return responseForOperations; 
	}
}