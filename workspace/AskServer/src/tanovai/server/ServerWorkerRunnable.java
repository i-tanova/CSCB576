package tanovai.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//Protocols starts with HI and ends with BYE message
public class ServerWorkerRunnable implements Runnable {
	private Socket socket;
	private static Map<String, List> themesList;
	private AskServer askServer;

	ServerWorkerRunnable(Map<String, List> themesList, AskServer askServer,
			Socket socket) {
		this.socket = socket;
		this.askServer = askServer;
		this.themesList = themesList;
	}

	@Override
	public void run() {
		BufferedReader read = null;
		PrintWriter printW = null;

		try {
			InputStream in = socket.getInputStream();
			read = new BufferedReader(new InputStreamReader(in));
			printW = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()), true);
			String line = null;
			StringBuffer request = new StringBuffer();
			while (((line = read.readLine()) != null)) {
				System.out.println("Request retrieved: " + line);
				if (line.equals("BYE")) {
					break;
				}
				if (line.equals(Constants.ASK_REQUEST_END)) {
					request.append(line);
					try {
						workRequest(request.toString());
					} finally {
						request = new StringBuffer();
						printW.println("BYE");
						System.out.println("BYE sended");
					}
				} else {
					request.append(line);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			printW.close();
			// connectionsQueue.remove(currentCon);
			try {
				socket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			System.out.println("Connection with client closed");
		}
	}

	private void workRequest(String requestStr)
			throws ParserConfigurationException, SAXException, IOException,
			Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setEncoding("UTF-8");
		is.setCharacterStream(new StringReader(requestStr));
		Document requestDoc = db.parse(is);
		requestDoc.getDocumentElement().normalize();

		Node first = requestDoc.getDocumentElement();
		if (!first.getNodeName().equals(Constants.ASK_REQUEST)) {
			throw new Exception("Invalid root element");
		}
		// checkRefresh(requestDoc, printW);

		int type = -1;
		NodeList nodeList = requestDoc.getElementsByTagName(Constants.TYPE);
		try {
			type = Integer.parseInt(nodeList.item(0).getTextContent());
		} catch (NumberFormatException ex) {
			throw new Exception("Invalid type request");
		}

		switch (type) {
		case Constants.ASK_REQ:
			workAskRequest(requestDoc);
			break;
		/*
		 * case Constants.PAUSE_REQ : workPauseRequest(requestDoc); break; case
		 * Constants.START_REQ: workStartRequest(line); break; case
		 * Constants.STOP_REQ: workStopRequest(line, printW); break;
		 */
		default:
			throw new Exception("Invalid type request");
		}

	}

	/*
	 * private void workStopRequest(String line, PrintWriter printW) { try {
	 * System.out.println("workStopRequest(String line)");
	 * printW.println(Constants.STOP_REQ); printW.println("BYE"); try {
	 * Thread.sleep(30000); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }finally{
	 * askServer.stopServer(); } } catch (IOException e) { //ignore
	 * e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * private void workPauseRequest(Document requestDoc) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * private void workStartRequest(String line) { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 */

	private void workAskRequest(Document requestDocs) throws Exception {
		System.out.println("tanovai.server.AskServer -> work ask request");
		NodeList requestThemeNode = requestDocs
				.getElementsByTagName(Constants.THEME);
		String theme = requestThemeNode.item(0).getTextContent();
		System.out.println(theme);
		System.out.println(themesList.keySet());
		if (!themesList.keySet().contains(theme)) {
			throw new Exception("Invalid theme");
		}

		String requestQuestion = requestDocs
				.getElementsByTagName(Constants.QUESTION).item(0)
				.getTextContent();
		String senderEmail = requestDocs
				.getElementsByTagName(Constants.SENDER_EMAIL).item(0)
				.getTextContent();
		List emails = themesList.get(theme);

		EmailSender.sendEmails(emails, senderEmail, requestQuestion);
	}

}
