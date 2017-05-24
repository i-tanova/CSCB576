package tanovai.server;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
	
 public static void sendEmails(List<String> toEmailsList, String senderEmail, String senderMessage) throws CoreException{
	    String host = "smtp.gmail.com";
	    String from = "tanovait";
	    String pass = "ooksip89";
	    
	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", "true"); // added this line
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.user", from);
	    props.put("mail.smtp.password", pass);
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.auth", "true");

	    String[] to = toEmailsList.toArray(new String[]{}); // added this line

	    Session session = Session.getDefaultInstance(props, null);
	    MimeMessage message = new MimeMessage(session);
	    try {
			message.setFrom(new InternetAddress(senderEmail));
		} catch (AddressException e) {
			throw new CoreException("Invalid sender email");
		} catch (MessagingException e) {
			throw new CoreException("Invalid sender message email");
		}

	    InternetAddress[] toAddress = new InternetAddress[Constants.EMAILS_COUNT];

	    // To get the array of addresses
	    for( int i=0; i < to.length && i< Constants.EMAILS_COUNT; i++ ) { // changed from a while loop
	        try {
				toAddress[i] = new InternetAddress(to[i]);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    System.out.println(Message.RecipientType.TO);

	    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
	        try {
	        	System.out.println(toAddress[i]);
				message.addRecipient(Message.RecipientType.TO, toAddress[i]);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    try {
			message.setSubject("Ask server request");
	
	    message.setText(senderMessage,"UTF-8");
	    Transport transport = session.getTransport("smtps");
	    transport.connect(host, from, pass);
	    transport.sendMessage(message, message.getAllRecipients());
	    transport.close();
		} catch (MessagingException e) {
			throw new CoreException("Error at sending email message");
		}
 }
 
  public static class MyAuthenticator extends Authenticator{
	  public PasswordAuthentication getPasswordAuthentication() {
		  return new PasswordAuthentication("tanovait", new char[]{'o','o','k','s','i','p','8','9'});
	  }
  }
}
