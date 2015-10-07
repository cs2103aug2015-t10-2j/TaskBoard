package com.taskboard.main;

public class TestDriver {
	public static void main(String[] args) {
		UserInterface _userInterface = new UserInterface();
		Logic _executor = new Logic();
		
		String currentCommand = _userInterface.getCommandReader().getNextCommand();
		while (!currentCommand.toLowerCase().equals("exit")) {
			Response currentResponse = _executor.processCommand(currentCommand);
			if (currentResponse.getIsSuccess()) {
				System.out.println(currentResponse.getFeedback());
//				System.out.println(_executor.retrieveEntries());
			} else {
				System.out.println(currentResponse.getException().getMessage());
			}
			currentCommand = _userInterface.getCommandReader().getNextCommand();
		}
	}
}
