package tanovai.server;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
 
 public static void sendEmails(List<String> toEmailsList, String senderEmail, String senderMessage) throws CoreException, AddressException, MessagingException{

	// Step1
			System.out.println("\n 1st ===> setup Mail Server Properties..");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.port", "587");
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			System.out.println("Mail Server Properties have been setup successfully..");
	 
			// Step2
			System.out.println("\n\n 2nd ===> get Mail Session..");
			getMailSession = Session.getDefaultInstance(mailServerProperties, new MyAuthenticator());
			generateMailMessage = new MimeMessage(getMailSession);
			String[] to = toEmailsList.toArray(new String[]{}); 
			
			for(String toString : to){
				generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toString));
			}
		    generateMailMessage.setFrom(Constants.EMAIL_ACCOUNT);
			generateMailMessage.setSubject("Ask server request");
			generateMailMessage.setContent(senderMessage, "text/html");
			System.out.println("Mail Session has been created successfully..");
	 
			// Step3
			System.out.println("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtp");
	 
			// Enter your correct gmail UserID and Password
			// if you have 2FA enabled then provide App Specific Password
			transport.connect(Constants.EMAIL_HOST, Constants.EMAIL_ACCOUNT, Constants.EMAIL_PASSWORD);
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
	 //	    String host = "smtp.gmail.com";
//	    String from = "tanovait";
//	    String pass = "ooksip89";
//	    
//	    Properties props = System.getProperties();
//	    props.put("mail.smtp.starttls.enable", "true"); // added this line
//	    props.put("mail.smtp.host", host);
//	    props.put("mail.smtp.user", from);
//	    props.put("mail.smtp.password", pass);
//	    props.put("mail.smtp.port", "587");
//	    props.put("mail.smtp.auth", "true");
//
//	    String[] to = toEmailsList.toArray(new String[]{}); 
//
//	    Session session = Session.getDefaultInstance(props, null);
//	    MimeMessage message = new MimeMessage(session);
//	    try {
//			message.setFrom(new InternetAddress(senderEmail));
//		} catch (AddressException e) {
//			throw new CoreException("Invalid sender email");
//		} catch (MessagingException e) {
//			throw new CoreException("Invalid sender message email");
//		}
//
//	    InternetAddress[] toAddress = new InternetAddress[Constants.EMAILS_COUNT];
//
//	    // To get the array of addresses
//	    for( int i=0; i < to.length && i< Constants.EMAILS_COUNT; i++ ) { // changed from a while loop
//	        try {
//				toAddress[i] = new InternetAddress(to[i]);
//			} catch (AddressException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    }
//	    System.out.println(Message.RecipientType.TO);
//
//	    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
//	        try {
//	        	System.out.println(toAddress[i]);
//				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
//			} catch (MessagingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    }
//	    
//	    try {
//			message.setSubject("Ask server request");
//	
//	    message.setText(senderMessage,"UTF-8");
//	    Transport transport = session.getTransport("smtps");
//	    transport.connect(host, from, pass);
//	    transport.sendMessage(message, message.getAllRecipients());
//	    transport.close();
//		} catch (MessagingException e) {
//			throw new CoreException("Error at sending email message");
//		}
 }
 
  public static class MyAuthenticator extends Authenticator{
	  protected  PasswordAuthentication 	getPasswordAuthentication(){
		  return new PasswordAuthentication("tanovait", Constants.EMAIL_PASSWORD);
	  }
	  
	  
  }
  
  public static void main(String[] args){
	  List<String> to = new LinkedList();
	  to.add("tanovait@gmail.com");
	  try {
		sendEmails(to, "Hi", "Hi");
	} catch (AddressException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (CoreException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
