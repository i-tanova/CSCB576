package rmi;

import java.rmi.*;
import java.util.List;
import java.util.Map;

public interface IServerAdminRMI extends Remote {

	public static final String SERVICE_NAME = "ServerAdmin";

	void startServer(int port) throws RemoteException;

	boolean isServerStarted() throws RemoteException;

	public void stopServer() throws RemoteException;

	public void pauseServer() throws RemoteException;

	public boolean isServerPaused() throws RemoteException;
	
	public Map<String, List> getThemes() throws RemoteException;
	
	public void setThemes(Map<String, List> themes) throws RemoteException;
}
