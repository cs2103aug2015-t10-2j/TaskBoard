package com.taskboard.main;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.util.logging.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class UserInterface extends JFrame {
	
	private static final String TITLE = "TaskBoard: Your Revolutionary Task Manager";
	private static final String TITLE_IMAGE_FILE_PATH = "resources/images/TaskBoard-title2-v03.png";
	private static final String DEFAULT_BACKGROUND_FILE_PATH = "resources/images/Black-Rose-Cool-Desktop-Background.jpg";
	private static final String HIGH_PRIORITY_FILE_PATH = "resources/images/HighPriority.jpg";
	private static final String MEDIUM_PRIORITY_FILE_PATH = "resources/images/MediumPriority.jpg";
	private static final String LOW_PRIORITY_FILE_PATH = "resources/images/LowPriority.jpg";
	private static final String NORMAL_PRIORITY_FILE_PATH = "resources/images/NormalPriority.jpg";
	
	private static final String MESSAGE_PROMPT_COMMAND = "Enter command below:";
	private static final String MESSAGE_NO_FEEDBACK = "No feedback to display.";
	
	private static final float DISPLAY_AREA_RELATIVE_TRANSPARENCY = 0.9f;
	private static final int LABEL_RELATIVE_TRANSPARENCY = 255;
	
	private static final String UP_BUTTON_CODE = "UP";
	private static final String DOWN_BUTTON_CODE = "DOWN";
	private static final String PAGE_UP_BUTTON_CODE = "PAGE_UP";
	private static final String PAGE_DOWN_BUTTON_CODE = "PAGE_DOWN";
	private static final String POSITIVE_SCROLL_CODE = "positiveUnitIncrement";
	private static final String NEGATIVE_SCROLL_CODE = "negativeUnitIncrement";
	
	private static final String EXIT_COMMAND_STRING = "exit";
	
	private static final long serialVersionUID = 1;
	
	private static UserInterface _instance;
	private Logic _logic;
	private JFrame _frame;
	private JLabel _backgroundPane;
	private JPanel _displayArea;
	private JScrollPane _displayScroll;
	private JScrollBar _verticalDisplayScroll;
	private JTextPane _feedbackArea;
	private JScrollPane _feedbackScroll;
	private JScrollBar _verticalFeedbackScroll;
	private JLabel _commandLabel;
	private JTextField _commandField;
	private JLabel _title;
	private String _backgroundFilePath;

	private static Logger _logger = GlobalLogger.getInstance().getLogger();
	
	private UserInterface() {
		_frame = new JFrame(TITLE);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.setPreferredSize(new Dimension(800, 720));
		_frame.addComponentListener(new WindowResizeListener());
		_frame.setVisible(true);
		
		_backgroundPane = new JLabel();
		_backgroundFilePath = DEFAULT_BACKGROUND_FILE_PATH;
		
		try {
			setBackground(_backgroundFilePath);
		} catch (IOException e) {
			if (_feedbackArea == null) {
				_feedbackArea = new JTextPane();
			}
			_feedbackArea.setText("Unexpected error during background resize.");
		}
		
		_backgroundPane.setLayout(new BorderLayout());
		_frame.setContentPane(_backgroundPane);
		
		initComponents(_frame.getContentPane());
		_frame.pack();
		
		_commandField.requestFocus();
	}

	public static UserInterface getInstance() {
		if (_instance == null) {
			_instance = new UserInterface();
		}
		return _instance;
	}
	
	public Logic getLogic() {
		return _logic;
	}
	
	public JTextField getCommandField() {
		return _commandField;
	}
	
	public String getBackgroundFilePath() {
		return _backgroundFilePath;
	}
	
	public JTextPane getFeedbackArea() {
		return _feedbackArea;
	}
	
	public void setBackground(String backgroundFileString) throws IOException {
		ImageIcon sourceIcon = new ImageIcon(ImageIO.read(new File(backgroundFileString)));
		Rectangle frameRect = _frame.getBounds();
		Image resizedImage = sourceIcon.getImage().getScaledInstance(frameRect.width, frameRect.height,  java.awt.Image.SCALE_SMOOTH);
		ImageIcon resizedIcon = new ImageIcon(resizedImage);
		_backgroundPane.setIcon(resizedIcon);
	}
	
	public void setFeedbackArea(JTextPane newFeedbackArea) {
		_feedbackArea = newFeedbackArea;
	}
	
	private void initComponents(Container pane) {
		pane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		_logic = new Logic();
		
		ImageIcon titleImage = new ImageIcon(TITLE_IMAGE_FILE_PATH);
		_title = new JLabel(titleImage);
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.weightx = 0.0;
		constraints.weighty = 0.1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		pane.add(_title, constraints);
		
		_displayArea = new TransparentPanel(DISPLAY_AREA_RELATIVE_TRANSPARENCY);
		_displayArea.setBackground(Color.WHITE);
		_displayArea.setLayout(new GridBagLayout());
		_displayArea.setAutoscrolls(true);
		constraints.weightx = 0.0;
		constraints.weighty = 0.5;
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 1;
		pane.add(_displayArea, constraints);

		_displayScroll = new TransparentScrollPane(_displayArea, DISPLAY_AREA_RELATIVE_TRANSPARENCY);
		_displayScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		_displayScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_displayScroll.getViewport().setPreferredSize(new Dimension(640, 400));
		_displayScroll.getVerticalScrollBar().setUnitIncrement(40);
		_displayScroll.setBackground(Color.WHITE);
		pane.add(_displayScroll, constraints);
		
		// Enable up and down buttons for scrolling.
		_verticalDisplayScroll = _displayScroll.getVerticalScrollBar();
		InputMap displayScrollIM = _verticalDisplayScroll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		displayScrollIM.put(KeyStroke.getKeyStroke(DOWN_BUTTON_CODE), POSITIVE_SCROLL_CODE);
		displayScrollIM.put(KeyStroke.getKeyStroke(UP_BUTTON_CODE), NEGATIVE_SCROLL_CODE);
		
		_feedbackArea = new JTextPane();
		_feedbackArea.setEditable(false);
		_feedbackArea.setAutoscrolls(false);
		_feedbackArea.setContentType("text/html");
		constraints.weightx = 0.0;
		constraints.weighty = 0.1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 2;
		pane.add(_feedbackArea, constraints);
		
		_feedbackScroll = new JScrollPane(_feedbackArea);
		_feedbackScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		_feedbackScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		_feedbackScroll.getViewport().setPreferredSize(new Dimension(640, 100));
		_feedbackScroll.getVerticalScrollBar().setUnitIncrement(20);
		pane.add(_feedbackScroll, constraints);
		
		// Enable up and down buttons for scrolling.
		_verticalFeedbackScroll = _feedbackScroll.getVerticalScrollBar();
		InputMap feedbackScrollIM = _verticalFeedbackScroll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		feedbackScrollIM.put(KeyStroke.getKeyStroke(PAGE_DOWN_BUTTON_CODE), POSITIVE_SCROLL_CODE);
		feedbackScrollIM.put(KeyStroke.getKeyStroke(PAGE_UP_BUTTON_CODE), NEGATIVE_SCROLL_CODE);

		_commandLabel = new JLabel(MESSAGE_PROMPT_COMMAND);
		_commandLabel.setFont(new Font("Sans-Serif", Font.ITALIC, 14));
		_commandLabel.setForeground(Color.WHITE);
		constraints.weightx = 0.0;
		constraints.weighty = 0.1;
		constraints.gridx = 0;
		constraints.gridy = 3;
		pane.add(_commandLabel, constraints);

		_commandField = new JTextField();
		_commandField.setEditable(true);
		_commandField.addKeyListener(new ShortcutListener());
		constraints.weightx = 0.0;
		constraints.weighty = 0.1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 4;
		pane.add(_commandField, constraints);

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UserInterface.getInstance();
			}
		});
	}
	
	public void executeInputCommand() {
		String userInput = _commandField.getText();

		if (userInput.toLowerCase().equals(EXIT_COMMAND_STRING)) {
			_logger.log(Level.INFO, "System exit.");
			System.exit(0);
		} else {
			Response currentResponse = getLogic().processCommand(userInput);
			
			if (currentResponse.isSuccess()) {
				ArrayList<Entry> entries = currentResponse.getEntries();
				
				if (entries != null) {
					_displayArea.removeAll();
					
					GridBagConstraints constraints = new GridBagConstraints();
					constraints.anchor = GridBagConstraints.PAGE_START;
					constraints.insets = new Insets(2, 2, 2, 2);
					constraints.fill = GridBagConstraints.NONE;
					
					String lastDate = "";
					
					int curGridY = 0;
					for (int i = 0; i < entries.size(); i++) {
						Entry currentEntry = entries.get(i);
						constraints.gridx = 0;
						constraints.gridy = curGridY;
						
						String pivotDate = "";
						if (currentEntry.getDateParameter() != null) {
							pivotDate = currentEntry.getDateParameter().getParameterValue();
						} else if (currentEntry.getStartDateParameter() != null) {
							pivotDate = currentEntry.getStartDateParameter().getParameterValue();
						} else if (currentEntry.getNameParameter() != null) {
							pivotDate = "Side Tasks";
						} else {
							pivotDate = "";
						}
						
						if (!lastDate.equals(pivotDate)) {
							constraints.gridwidth = 2;
							JLabel dateLabel = new JLabel();
							dateLabel.setText(pivotDate);
							dateLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
							dateLabel.setBackground(Color.WHITE);
							dateLabel.setOpaque(true);
							_displayArea.add(dateLabel, constraints);
							lastDate = pivotDate;
							constraints.gridx = 0;
							constraints.gridy = ++curGridY;
							constraints.gridwidth = 1;
						}
						
						if (i == entries.size() - 1) {
							constraints.weighty = 1;
						}
						
						JLabel indexLabel = new JLabel();
						if (currentEntry.getIndexParameter() != null) {
							indexLabel.setText(currentEntry.getIndexParameter().getParameterValue() + '.');
						}
						indexLabel.setVerticalAlignment(JLabel.TOP);
						indexLabel.setPreferredSize(new Dimension(30, 30));
						indexLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
						indexLabel.setOpaque(true);
						
						if (currentEntry.getDateParameter() != null) {
							indexLabel.setBackground(new Color(255, 192, 203, LABEL_RELATIVE_TRANSPARENCY));
							_displayArea.add(indexLabel, constraints);
							
							constraints.gridx = 1;
							constraints.gridy = curGridY++;
							JTextPane deadlineLabel = new JTextPane();
							deadlineLabel.setEditable(false);
							
							JLabel deadlineIcon = new JLabel();
							deadlineIcon.setBounds(368, 0, 112, 27);
							assignPriorityIcon(currentEntry, deadlineIcon);
							deadlineLabel.add(deadlineIcon);
							
							JTextArea deadlineText = new TransparentTextArea(1.0f);
							deadlineText.setText(currentEntry.toUIString());
							deadlineText.setFont(UIManager.getFont("Label.font"));
							deadlineText.setLineWrap(true);
							deadlineText.setBorder(new EmptyBorder(5, 5, 5, 5));
							deadlineText.setBounds(0, 0, 320, 64);
							deadlineText.setPreferredSize(new Dimension(320, 64));
							deadlineLabel.add(deadlineText);
							
							if (currentEntry.getCategoryParameter() != null) {
								JTextArea categoryText = new JTextArea();
								categoryText.setText(currentEntry.getCategoryParameter().getParameterValue());
								categoryText.setFont(UIManager.getFont("Label.font"));
								categoryText.setLineWrap(true);
								categoryText.setBorder(new EmptyBorder(5, 5, 5, 5));
								categoryText.setBounds(320, 27, 160, 37);
								categoryText.setPreferredSize(new Dimension(160, 37));
								categoryText.setBackground(new Color(245, 182, 193, LABEL_RELATIVE_TRANSPARENCY));
								categoryText.setOpaque(true);
								deadlineLabel.add(categoryText);
							}
							
							deadlineLabel.setBackground(new Color(255, 192, 203, LABEL_RELATIVE_TRANSPARENCY));
							deadlineLabel.setOpaque(true);
							deadlineLabel.setPreferredSize(new Dimension(480, 64));
							deadlineLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
							
							_displayArea.add(deadlineLabel, constraints);
						} else if (currentEntry.getStartDateParameter() != null) {
							indexLabel.setBackground(new Color (175, 255, 163, LABEL_RELATIVE_TRANSPARENCY));
							_displayArea.add(indexLabel, constraints);
							
							constraints.gridx = 1;
							constraints.gridy = curGridY++;
							JTextPane eventLabel = new JTextPane();
							eventLabel.setEditable(false);
							
							JLabel eventIcon = new JLabel();
							eventIcon.setBounds(368, 0, 112, 27);
							assignPriorityIcon(currentEntry, eventIcon);
							eventLabel.add(eventIcon);
							
							JTextArea eventText = new TransparentTextArea(1.0f);
							eventText.setText(currentEntry.toUIString());
							eventText.setFont(UIManager.getFont("Label.font"));
							eventText.setLineWrap(true);
							eventText.setBorder(new EmptyBorder(5, 5, 5, 5));
							eventText.setBounds(0, 0, 320, 64);
							eventText.setPreferredSize(new Dimension(320, 64));
							eventLabel.add(eventText);
							
							if (currentEntry.getCategoryParameter() != null) {
								JTextArea categoryText = new JTextArea();
								categoryText.setText(currentEntry.getCategoryParameter().getParameterValue());
								categoryText.setFont(UIManager.getFont("Label.font"));
								categoryText.setLineWrap(true);
								categoryText.setBorder(new EmptyBorder(5, 5, 5, 5));
								categoryText.setBounds(320, 27, 160, 37);
								categoryText.setPreferredSize(new Dimension(160, 37));
								categoryText.setBackground(new Color(165, 245, 153, LABEL_RELATIVE_TRANSPARENCY));
								categoryText.setOpaque(true);
								eventLabel.add(categoryText);
							}
							
							eventLabel.setBackground(new Color (175, 255, 163, LABEL_RELATIVE_TRANSPARENCY));
							eventLabel.setOpaque(true);
							eventLabel.setPreferredSize(new Dimension(480, 64));
							eventLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
							
							_displayArea.add(eventLabel, constraints);
						} else if (currentEntry.getNameParameter() != null) {
							indexLabel.setBackground(new Color (198, 255, 250, LABEL_RELATIVE_TRANSPARENCY));
							_displayArea.add(indexLabel, constraints);
							
							constraints.gridx = 1;
							constraints.gridy = curGridY++;
							JTextPane floatLabel = new JTextPane();
							floatLabel.setEditable(false);
							
							JLabel floatIcon = new JLabel();
							floatIcon.setBounds(368, 0, 112, 27);
							assignPriorityIcon(currentEntry, floatIcon);
							floatLabel.add(floatIcon);
							
							JTextArea floatText = new TransparentTextArea(1.0f);
							floatText.setText(currentEntry.toUIString());
							floatText.setFont(UIManager.getFont("Label.font"));
							floatText.setLineWrap(true);
							floatText.setBorder(new EmptyBorder(5, 5, 5, 5));
							floatText.setBounds(0, 0, 320, 64);
							floatText.setPreferredSize(new Dimension(320, 64));
							floatLabel.add(floatText);
							
							if (currentEntry.getCategoryParameter() != null) {
								JTextArea categoryText = new JTextArea();
								categoryText.setText(currentEntry.getCategoryParameter().getParameterValue());
								categoryText.setFont(UIManager.getFont("Label.font"));
								categoryText.setLineWrap(true);
								categoryText.setBorder(new EmptyBorder(5, 5, 5, 5));
								categoryText.setBounds(320, 27, 160, 37);
								categoryText.setPreferredSize(new Dimension(160, 37));
								categoryText.setBackground(new Color(188, 245, 240, LABEL_RELATIVE_TRANSPARENCY));
								categoryText.setOpaque(true);
								floatLabel.add(categoryText);
							}
							
							floatLabel.setBackground(new Color (198, 255, 250, LABEL_RELATIVE_TRANSPARENCY));
							floatLabel.setOpaque(true);
							floatLabel.setPreferredSize(new Dimension(480, 64));
							floatLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
							
							_displayArea.add(floatLabel, constraints);
						} else {
							constraints.gridx = 0;
							constraints.gridy = curGridY++;
							constraints.gridwidth = 2;
							JTextPane helpLabel = new JTextPane();
							helpLabel.setEditable(false);
							helpLabel.setText(currentEntry.toUIString());
							helpLabel.setBackground(new Color(255, 165, 0, LABEL_RELATIVE_TRANSPARENCY));
							helpLabel.setOpaque(true);
							helpLabel.setPreferredSize(new Dimension(480, 320));
							helpLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
							_displayArea.add(helpLabel, constraints);
							constraints.gridwidth = 1;
						}
					}
					
					_displayArea.revalidate();
					_displayArea.repaint();
					_displayScroll.revalidate();
					_displayScroll.repaint();
				}
				
				_logger.log(Level.INFO, "Successfully updated display area.");
				
				_feedbackArea.setForeground(new Color(50, 205, 50));
			} else {
				_feedbackArea.setForeground(new Color(178, 34, 34));
			}
			
			if (currentResponse.getFeedback() != null) {
				String feedback = currentResponse.getFeedback().trim();
				_feedbackArea.setText(feedback);
				_feedbackArea.setCaretPosition(0);
				
				_logger.log(Level.INFO, "Successfully updated feedback area.");
			} else {
				_feedbackArea.setText(MESSAGE_NO_FEEDBACK);
				_feedbackArea.setCaretPosition(0);
				
				_logger.log(Level.INFO, "Successfully updated feedback area.");
			}
		}

		_commandField.setText("");
	}
	
	private static void assignPriorityIcon(Entry currentEntry, JLabel currentLabel) {
		Parameter priority = currentEntry.getPriorityParameter();
		if (priority != null) {
			switch (priority.getParameterValue()) {
				case "high":
					currentLabel.setIcon(new ImageIcon(HIGH_PRIORITY_FILE_PATH));
					break;
				case "medium":
					currentLabel.setIcon(new ImageIcon(MEDIUM_PRIORITY_FILE_PATH));
					break;
				case "low":
					currentLabel.setIcon(new ImageIcon(LOW_PRIORITY_FILE_PATH));
					break;
			}
		} else {
			currentLabel.setIcon(new ImageIcon(NORMAL_PRIORITY_FILE_PATH));
		}
	}
	
}