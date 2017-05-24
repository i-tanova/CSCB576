package tanovai.server;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rmi.IServerAdminRMI;

public class AskServerMain implements IServerAdminRMI {

	private static Map<String, List> themesList = new HashMap<String, List>();
	static{
		initConfiguration();
	}
	private static int configVersion;
	private AskServer server;

	public static void main(String[] args) {
		grantPolicy();
		registerRMIStub();
	}
	
	private static void registerRMIStub(){
		try {
			Registry registry = LocateRegistry.createRegistry(Constants.rmiPort);
			AskServerMain serverStarter = new AskServerMain();
			UnicastRemoteObject.exportObject(serverStarter, 0);
			registry.rebind(AskServerMain.SERVICE_NAME, serverStarter);
			System.out.println("Service Bound...");
		} catch (Exception e) {
			System.out.println("An error occured trying to bind the object to the registry.\n" + e);
		}
	}
	
	public static void grantPolicy(){
		System.setProperty("java.rmi.server.codebase",
				AskServerMain.class.getProtectionDomain()
						.getCodeSource().getLocation().toString());

		System.setProperty("java.security.policy",
				"/home/ivana/Ivana/Develop/NBU/AskServer2/Resources/server.policy");

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}

	public static void initConfiguration() {
		try {
			File resources = new File(Constants.configDir + File.separator + Constants.configFile);
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

	@Override
	public void startServer(int port) throws RemoteException {
		if(server.isPaused()){
			server.startAfterPaused();
			return;
		}
		
		server = new AskServer(themesList, port);

		new Thread(server).start();

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}

	@Override
	public boolean isServerStarted() throws RemoteException {
		if (server != null) {
			return !server.isStopped();
		}
		return false;
	}

	@Override
	public void stopServer() throws RemoteException {
		if (server != null) {
			server.stop();
		}
	}

	@Override
	public void pauseServer() throws RemoteException {
		if (server != null) {
			server.pause();
		}

	}

	@Override
	public boolean isServerPaused() throws RemoteException {
		if (server != null) {
			return server.isPaused();
		}
		return false;
	}

	@Override
	public Map<String, List> getThemes() throws RemoteException {
		return themesList;
	}

	@Override
	public void setThemes(Map<String, List> themes) throws RemoteException {
		this.themesList = themes;
	}

}
