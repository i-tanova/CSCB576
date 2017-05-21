package tanovai.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class that is server for requests. Every request starts new thread
 * 
 * @author ivana
 *
 */
public class AskServer implements Runnable {

	private ServerSocket servSocket;
	private boolean isStopped = true;
	private boolean isPaused = false;
	int port;

	public AskServer(int port) {
		this.port = port;
	}

	// parameters for configuration
	private static int configVersion = -1;
	// maps each theme to list of email receivers
	private static Map<String, List> themesList = new HashMap<String, List>();
	static {
		initializeFromConfigXML();
	}

	/**
	 * Starts the server at this port. Each request is handled at separated
	 * thread.
	 * 
	 * @param port
	 */
	public void run() {
		System.out.println("tanovai.server.AskServer -> Start server");
		try {
			servSocket = new ServerSocket(port);
			while (!isStopped()) {
				if (!isPaused) {
					System.out.println("Server is running");
					new Thread(new ServerWorkerRunnable(themesList, this, servSocket.accept())).start();
				} else {
					System.out.println("Server is paused!");
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			if (isStopped()) {
				System.out.println("Server Stopped!");
				return;
			}
		}
	}

	public void stopServer() throws IOException {
		isStopped = true;
		this.servSocket.close();
	}

	public boolean isStopped() {
		return isStopped;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public static void initializeFromConfigXML() {
		try {
			File resources = new File(Constants.resourceLocation);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = null;
			doc = db.parse(resources);
			doc.getDocumentElement().normalize();
			NodeList versionNode = doc.getElementsByTagName(Constants.VERSION);
			try {
				configVersion = Integer.parseInt(versionNode.item(0).getTextContent());
			} catch (NumberFormatException ex) {
				// throw new CoreException("Invalid version");
			}
			NodeList themesNode = doc.getElementsByTagName(Constants.THEME);
			themesList.clear();
			for (int i = 0; i < themesNode.getLength(); i++) {
				Node theme = themesNode.item(i);
				if (theme instanceof Element) {
					NodeList themeChilds = theme.getChildNodes();
					List themeEmails = new ArrayList();
					for (int j = 0; j < themeChilds.getLength(); j++) {
						if (themeChilds.item(j) instanceof Element) {
							themeEmails.add(((Element) themeChilds.item(j)).getTextContent());
						}
					}
					themesList.put(theme.getFirstChild().getTextContent().trim(), themeEmails);
				}
			}
			for (Iterator iterator = themesList.keySet().iterator(); iterator.hasNext();) {
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

	public void pause() {
		isPaused = true;
	}

	// starts after pause
	public void start() {
		isPaused = false;
		isStopped = false;
	}
}
