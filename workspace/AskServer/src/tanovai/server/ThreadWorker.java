package tanovai.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.mail.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ThreadWorker extends Thread {
	
  private Queue<Socket> connectionsQueue = null;
  
  private Socket currentCon = null;
  
  private File resource = new File("Resources", "resource.txt");
  
 private static int configVersion = -1;
 
 private static Map<String, List> themesList = new HashMap<String, List>();
 
 private static Document doc = null;
 
 static{
	 initializeFromConfigXML();
 }
  
	 public ThreadWorker(Queue<Socket>connections, Socket currentCon){
		 this.connectionsQueue = connections;
		 connections.add(currentCon);
		 this.currentCon = currentCon;
	 }
	 
	 public void run(){	 
		 BufferedReader read = null;
		 PrintWriter printW = null;	 
		
		 try{		
		 InputStream in =  currentCon.getInputStream();
		 read = new BufferedReader(new InputStreamReader(in));
		 printW  = new PrintWriter(new OutputStreamWriter(currentCon.getOutputStream()), true); 
		 String line = null; 
		 
		 while (!((line = read.readLine()).equals("BYE")) ){
			 System.out.println("Request retrieved..");
			 
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  InputSource is = new InputSource( );
			  is.setEncoding("UTF-8");
			  is.setCharacterStream(new StringReader(line) );
			  Document requestDoc = db.parse(is);
			  requestDoc.getDocumentElement().normalize();
			
			  Node first = requestDoc.getDocumentElement();
				if(! first.getNodeName().equals(Constants.ASK_REQUEST)){
					throw new CoreException("Invalid root element");		
				}
				// checkRefresh(requestDoc, printW);
				
				int type = -1;
			  NodeList nodeList = requestDoc.getElementsByTagName(Constants.TYPE);
			  try{
			    type = Integer.parseInt(nodeList.item(0).getTextContent());
			  }catch(NumberFormatException ex){
				  throw new CoreException("Invalid type request");
			  }

			    switch (type){
			    case Constants.ASK_REQ :
			    	workAskRequest(requestDoc);
			    	break;
			    case Constants.PAUSE_REQ :
			      workPauseRequest(requestDoc);
			      break;
			    case Constants.START_REQ:
			    	workStartRequest(line);
			    	break;
			    case Constants.STOP_REQ:
			    	workStopRequest(line);
			    	break;
			    default:
			    	throw new CoreException("Invalid type request");    
			    }
			    
			    printW.println("BYE");
			    System.out.println("BYE sended");
			  
		 }
		 }catch(Exception e){
				 e.printStackTrace();
			 }
		 finally{
			 printW.close();
			 connectionsQueue.remove(currentCon);
			 try{
				currentCon.close();
			 }catch (IOException ioe){
				 ioe.printStackTrace();
			 }
			 try{
				read.close();
			 } catch (IOException ioe){
				 ioe.printStackTrace();
			 }
		 }
	 }
		  
	 private void checkRefresh(Document doc, PrintWriter printW) throws CoreException {
		NodeList versionNode = doc.getElementsByTagName(Constants.VERSION);
		try{
		int userVersion = Integer.parseInt(versionNode.item(0).getTextContent());
		if(userVersion != configVersion ){
			makeRefreshRequest(printW);
		}
		}catch(NumberFormatException ex){
			throw new CoreException("Invalid version");
		}
		
	}

	private void makeRefreshRequest(PrintWriter printW) {
	    String response = "<ASK_RESPONSE><TYPE>1</TYPE>";
	    for(int i=0; i<themesList.size(); i++){
	    	response+="<THEME>" + themesList.get(i) +"</THEME>";
	    }
	    response+="</ASK_RESPONSE>";
		printW.println(response);	
	}

	private void workStopRequest(String line) {
		// TODO Auto-generated method stub
		
	}

	private void workPauseRequest(Document requestDoc) {
		// TODO Auto-generated method stub
		
	}

	private void workStartRequest(String line) {
		// TODO Auto-generated method stub
		
	}

	private void workAskRequest(Document requestDocs) throws CoreException {
		NodeList requestThemeNode = requestDocs.getElementsByTagName(Constants.THEME);
		String theme = requestThemeNode.item(0).getTextContent();
		System.out.println(theme);
		System.out.println(themesList.keySet());
		if(!themesList.keySet().contains(theme)){
			throw new CoreException("Invalid theme");
		}
		
	   String requestQuestion = requestDocs.getElementsByTagName(Constants.QUESTION).item(0).getTextContent();
	   String senderEmail =  requestDocs.getElementsByTagName(Constants.SENDER_EMAIL).item(0).getTextContent();
	   List emails = themesList.get(theme);
	
		EmailSender.sendEmails(emails, senderEmail, requestQuestion);
	}
	 
	 public static void initializeFromConfigXML(){
			try {
				 File resources = new File(Constants.resourceLocation);
				  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				  DocumentBuilder db = dbf.newDocumentBuilder();
				  doc = db.parse(resources);
				  doc.getDocumentElement().normalize();
				  NodeList versionNode = doc.getElementsByTagName(Constants.VERSION);
				  try{
						configVersion = Integer.parseInt(versionNode.item(0).getTextContent());
						}catch(NumberFormatException ex){
						//	throw new CoreException("Invalid version");
						}
				  NodeList themesNode = doc.getElementsByTagName(Constants.THEME);
				  themesList.clear();
				  for(int i=0;i<themesNode.getLength(); i++){
					  Node theme = themesNode.item(i);
					 if(theme instanceof Element){
					  NodeList themeChilds = theme.getChildNodes();
					  List themeEmails = new ArrayList();
					  for (int j=0; j<themeChilds.getLength(); j++){
						  if(themeChilds.item(j) instanceof  Element){
						  themeEmails.add(((Element)themeChilds.item(j)).getTextContent());
						  }
					  }
					  themesList.put(theme.getFirstChild().getTextContent().trim(), themeEmails);
					 }
				  }
					 for (Iterator iterator = themesList.keySet().iterator(); iterator
							.hasNext();) {
						String key = (String) iterator.next();
						System.out.println("theme:" + key);
					    List<String> emails = themesList.get(key);
					    for (String email : emails) {
							System.out.println("email " + email);
						}
						
					}
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 }
}
