package tanovai.server;
import java.util.ArrayList;
import java.util.Date;
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
	new SendEmailThread(toEmailsList, senderEmail, senderMessage).start();
 }
 
   public static class SendEmailThread extends Thread{
	   private List<String> toEmailsListFinal = new ArrayList<String>();
	   private String senderEmailFinal = "";
	   private String senderMessageFinal = "";
			   
	   SendEmailThread(List<String> toEmailsList, String senderEmail, String senderMessage){
		   this.toEmailsListFinal = toEmailsList;
		   this.senderEmailFinal = senderEmail;
		   this.senderMessageFinal = senderMessage;
	   }
	   public void run() {
		    String d_port = "587";
		    String d_host = "smtp.gmail.com";
		    String d_user = Constants.EMAIL_ACCOUNT;
			// Step1
			System.out.println("\n 1st ===> setup Mail Server Properties..");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.port", d_port);
			mailServerProperties.put("mail.smtp.host", d_host);
			mailServerProperties.put("mail.smtp.user", d_user);
			
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			
			
//			//Try
//			mailServerProperties.put("mail.smtp.socketFactory.port", d_port);
//			mailServerProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//			mailServerProperties.put("mail.smtp.socketFactory.fallback", "false");
//			
//			//
		
			
			System.out.println("Mail Server Properties have been setup successfully..");
	 
			// Step2
			System.out.println("\n\n 2nd ===> get Mail Session..");
			
			getMailSession = Session.getInstance(mailServerProperties,
					new MyAuthenticator());
	
			getMailSession.setDebug(true);
			
			generateMailMessage = new MimeMessage(getMailSession);
			String[] to = toEmailsListFinal.toArray(new String[]{}); 
			
			for(String toString : to){
				try {
					generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toString));
				} catch (AddressException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		    
			try {
				generateMailMessage.setFrom(new InternetAddress(Constants.EMAIL_ACCOUNT));
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				generateMailMessage.setSubject("Ask server request");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				generateMailMessage.setContent(senderMessageFinal, "text/html");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Mail Session has been created successfully..");
	 
			// Step3
			System.out.println("\n\n 3rd ===> Get Session and Send mail");
			//Transport transport = getMailSession.getTransport("smtp");
	 
			// Enter your correct gmail UserID and Password
			// if you have 2FA enabled then provide App Specific Password
			//transport.connect(Constants.EMAIL_HOST, Constants.EMAIL_ACCOUNT, Constants.EMAIL_PASSWORD);
			//transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			//transport.close();
			
			try {
				Transport.send(generateMailMessage);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
   }
 
 
// public static void sendEmailTest(List<String> toEmailsList, String senderEmail, String senderMessage) {
//	 
//
//     Properties props = new Properties();
////here you set the host information (information about who is sending the email)
////in this case, who is sending the email is a gmail client...
//     props.put("mail.smtp.host", "smtp.gmail.com");
//     props.put("mail.smtp.socketFactory.port", "465");
//     props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
//     props.put("mail.smtp.auth", "true");
//     props.put("mail.smtp.port", "465");
//
//     Session ses2 = Session.getDefaultInstance(props,
//             new javax.mail.Authenticator() {
//                 protected PasswordAuthentication        getPasswordAuthentication() {
//                     return new PasswordAuthentication(Constants.EMAIL_ACCOUNT,Constants.EMAIL_PASSWORD);
//                 }
//             });
//
//     try {
//
//
//         Message msg = new MimeMessage(ses2); 
//
//         msg.setFrom(new InternetAddress(senderEmail));
//         msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmailsList.get(0)));
//         msg.setSentDate(new Date());  
//         msg.setSubject("Subject");  
//         msg.setText(senderMessage);
//
//      // sending message (trying)  
//         Transport.send(msg);  
//
//     } catch (AddressException e) {
//         e.printStackTrace();
//     } catch (MessagingException e) {
//         e.printStackTrace();
//     } 
// }
 
  public static class MyAuthenticator extends Authenticator{
	  protected  PasswordAuthentication getPasswordAuthentication(){
		  return new PasswordAuthentication(Constants.EMAIL_FROM, Constants.EMAIL_PASSWORD);
	  }
	  
  }
  /**
   *  Test sending of mails only
   * @param args
   */
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
