package rmi;

import java.rmi.*;

public interface IServerAdminRMI extends Remote {

	public static final String SERVICE_NAME = "ServerStarter";

	void startServer(int port) throws RemoteException;

	boolean isServerStarted() throws RemoteException;

	public void stopServer() throws RemoteException;

	public void pauseServer() throws RemoteException;

	public boolean isServerPaused() throws RemoteException;
}
