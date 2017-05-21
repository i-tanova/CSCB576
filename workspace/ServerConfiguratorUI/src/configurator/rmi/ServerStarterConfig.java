package configurator.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import configurator.admin.Constants;
import configurator.admin.CoreException;

import rmi.IServerAdminRMI;
import rmi.RMIStarter;
//This starts server stub
//First execute "start rmiregistry"

public class ServerStarterConfig {

	private IServerAdminRMI serverStarter;
	private static ServerStarterConfig serverStarterConfig;

	private ServerStarterConfig() {
	}

	private void initServerStarter() throws RemoteException, NotBoundException {
		Registry registry;
		registry = LocateRegistry.getRegistry(12346);
		serverStarter = (IServerAdminRMI) registry.lookup(IServerAdminRMI.SERVICE_NAME);
	}

	public void startServer(final int port) throws Exception {
		new Thread() {
			public void run() {
				System.out.println("configurator.rmi.ServerStarterConfig.startServer(int port)");
				if (serverStarter == null) {
					try {
						initServerStarter();
					} catch (RemoteException | NotBoundException e) {
						e.printStackTrace();
						return;
					}
				}
				try {
					serverStarter.startServer(port);
				} catch (RemoteException e) {
					String message = "configurator.rmi.ServerStarterConfig ->Cannot start server.";
					try {
						throw new CoreException(message);
					} catch (CoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		}.run();

	}

	public void stopServer() {
		if (serverStarter != null) {
			try {
				serverStarter.stopServer();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean isServerStarted() throws RemoteException {
		if (serverStarter != null) {
			return serverStarter.isServerStarted();
		} else {
			return false;
		}
	}

	public boolean isServerPaused() throws RemoteException {
		if (serverStarter != null) {
			return serverStarter.isServerPaused();
		} else {
			return false;
		}
	}

	public static ServerStarterConfig getServerStarterConfigurator() {
		if (serverStarterConfig == null) {
			serverStarterConfig = new ServerStarterConfig();
		}
		return serverStarterConfig;
	}

	public void pauseServer() {
		if (serverStarter != null) {
			try {
				serverStarter.pauseServer();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
