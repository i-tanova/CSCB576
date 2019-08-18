package tanovai.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AskServer implements Runnable {

	protected int serverPort;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	private boolean isPaused = false;

	protected Thread runningThread = null;
	private static Map<String, List> themesList;
	
	public AskServer(Map<String, List> themesList, int port) {
		super();
		this.serverPort = port;
		this.themesList = themesList;
	}

	@Override
	public void run() {
		System.out.println("Server Started.");
		
		sendMail();

		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}

		openServerSocket();

		while (!isStopped()) {
			Socket clientSocket = null;
			if(isPaused){
				System.out.println("Server is paused.");
				return;
			}
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (isStopped()) {
					System.out.println("Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}

			new Thread(new WorkerRunnable(clientSocket, themesList)).start();
		}

		System.out.println("Server Stopped.");
	}

	private void sendMail() {
		ArrayList<String> list = new ArrayList();
		list.add("tanovait@gmail.com");
		try {
			EmailSender.sendEmails(list, "tanovait@gmail.com", "Q");
		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}
	
	public synchronized boolean isPaused(){
		return isPaused;
	}
	
	
	public synchronized void pause() {
		this.isPaused = true;
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port" + this.serverPort, e);
		}
	}

	public void startAfterPaused() {
		isPaused = false;
	}

}
