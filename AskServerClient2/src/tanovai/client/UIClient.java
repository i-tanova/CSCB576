package tanovai.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import tanovai.client.Constants;

public class UIClient {

	private static JButton askButton;
	private static JComboBox<String> themes;
	private static JTextField emailTxt;
	private static JTextPane questionPane;
	
	public static void main(String[] args) {		
		JFrame f = new JFrame("JDesktopPane Sample");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();

		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(Box.createRigidArea(new Dimension(0, 15)));
		
		JPanel themesPanel = createThemesPanel();
		content.add(themesPanel );
		content.add(Box.createRigidArea(new Dimension(0, 15)));
		
		JPanel emailPanel = createEmailPanel();
		content.add(emailPanel);
		content.add(Box.createRigidArea(new Dimension(0, 15)));
		
		JPanel questionPanel = createQuestionTextPanel();
		content.add(questionPanel);
		content.add(Box.createRigidArea(new Dimension(0, 15)));
		
		Panel askBPanel = createAskButtonPanel();
		content.add(askBPanel);
		content.add(Box.createRigidArea(new Dimension(0, 15)));
        
		f.setSize(500, 500);
		f.setVisible(true);
	}
	
//	public static void grantPolicy(){
//		System.setProperty("java.security.policy",
//				System.getProperty("user.dir")+ "/Resources/server.policy");
//
//		if (System.getSecurityManager() == null) {
//			System.setSecurityManager(new SecurityManager());
//		}
//	}

	
	private static void makeRequest(String theme, String email, String question){
		Client client = new Client();
		try {
			Socket connection = client.connectToServer(Constants.serverHost, Constants.serverPort);
			System.out.println("Ask client connected");
			String request = buildXML(theme, email, question);
			client.writeToSocket(connection, request);
			
			String response = client.readFromSocket(connection);
			System.out.println("Response " + response);
			
			String endMsg = "BYE";
			client.writeToSocket(connection, endMsg);
			client.closeConnection(connection);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
//	private static Panel createUserPanel() {
//		Panel themesPanel = createThemesPanel();
//		Panel emailPanel = createEmailPanel();
//		Panel questionPanel = createQuestionTextPanel();
//		Panel askBPanel = createAskButtonPanel();
//		
//		Panel userPanel = new Panel(new GridLayout(3, 3));
//		userPanel.add(themesPanel);
//		userPanel.add(emailPanel);
//		userPanel.add(questionPanel);
//		userPanel.add(askBPanel);
//		userPanel.setSize(300, 300);	
//		return userPanel;
//	}
	
	
	private static JPanel createQuestionTextPanel() {
		JPanel questionPanel = new JPanel(new  GridLayout(2, 1));
		questionPanel.setMaximumSize(new Dimension(450, 100));
		
		JLabel label = new JLabel("Enter question here:");
		questionPanel.add(label);
		
		questionPane = new JTextPane();
		questionPane.setContentType("text/html");
		questionPanel.add(questionPane);
		return questionPanel ;
	}

	private static JPanel createThemesPanel() {		
		JPanel themesPanel = new JPanel();
		themesPanel.setLayout(new BorderLayout(0, 0));
		themesPanel.setMaximumSize(new Dimension(450, 0));
		
		JLabel nameLabel = new JLabel("Choose question topic:");
		themesPanel.add(nameLabel);
		
	    themes = new JComboBox<String>(new String[] {Constants.theme1, Constants.theme2, Constants.theme3 });
		themes.setSize(60, 60);
		themesPanel.add(themes, BorderLayout.EAST);
		return themesPanel;
	}
	
	private static JPanel createEmailPanel() {
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new BorderLayout(0, 0));
		emailPanel.setMaximumSize(new Dimension(450, 0));
		
		JLabel label = new JLabel("Enter your email:");
		emailPanel.add(label);
		
		emailTxt = new JTextField();
		emailTxt.setSize(60, 60);
	    emailPanel.add(emailTxt, BorderLayout.PAGE_END);
		return emailPanel;
	}
	
	
	private static Panel createAskButtonPanel() {
		Panel sendButtonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
		sendButtonPanel.setPreferredSize(new Dimension(250, 150));
		askButton = new JButton("Ask");
		askButton.setSize(50, 200);
		askButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String theme = themes.getItemAt(0);
				String email = emailTxt.getText();
				String question = questionPane.getText();
				
				makeRequest(theme, email, question);
				}
		});
		sendButtonPanel.add(askButton);
	
		return sendButtonPanel;
	}
	
	
	static String buildXML(String theme, String email, String question)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream( bos );
        ps.println(getOpeningTag(Constants.ASK_REQUEST));
        ps.println(getOpeningTag(Constants.TYPE) + Constants.ASK_REQ + getClosingTag(Constants.TYPE) );
        ps.println(getOpeningTag(Constants.THEME) + theme + getClosingTag(Constants.THEME));
        ps.println(getOpeningTag(Constants.QUESTION) + question + getClosingTag(Constants.QUESTION));
        ps.println(getOpeningTag(Constants.SENDER_EMAIL) + email + getClosingTag(Constants.SENDER_EMAIL));
        ps.println(getClosingTag(Constants.ASK_REQUEST));
        String xml = bos.toString();
        return xml;
    } // buildXML
	
	static String getOpeningTag(String tag){
		return "<" + tag +">";
	}
	
	static String getClosingTag(String tag){
		return "</" + tag +">";
	}
}
