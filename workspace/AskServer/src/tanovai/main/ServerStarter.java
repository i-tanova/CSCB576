package tanovai.main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import rmi.IServerAdminRMI;
import tanovai.server.Constants;
import tanovai.server.ProjectServer;

public class ServerStarter extends UnicastRemoteObject implements IServerAdminRMI{

	protected ServerStarter() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// creates an instance of the remote object implementation, exports the
	// remote object, and then binds that instance to a name
	public static void main(String[] args) {
		registerRMIStub();
	}
	
	private static void registerRMIStub(){
		try {
			Registry registry = LocateRegistry.createRegistry(Constants.port);
			ServerStarter serverStarter = new ServerStarter();
			registry.rebind(ServerStarter.SERVICE_NAME, serverStarter);
			System.out.println("Service Bound...");
		} catch (Exception e) {
			System.out.println("An error occured trying to bind the object to the registry.\n" + e);
		}
	}

	@Override
	public void startServer(int port) throws RemoteException {
		ProjectServer.startNewServer(port);
	}

	@Override
	public boolean isServerStarted() throws RemoteException {
		return true;
	}

	@Override
	public void stopServer() throws RemoteException {
		ProjectServer.stopServer();
	}

	@Override
	public void pauseServer() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isServerPaused() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

}
