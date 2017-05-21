package rmi;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public abstract class RMIStarter {

	/**
	 * 
	 * @param clazzToAddToServerCodebase
	 *            a class that should be in the java.rmi.server.codebase
	 *            property.
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public RMIStarter(Class clazzToAddToServerCodebase) throws MalformedURLException, RemoteException, NotBoundException {
		System.setProperty("java.rmi.server.codebase",
				clazzToAddToServerCodebase.getProtectionDomain()
						.getCodeSource().getLocation().toString());

		System.setProperty("java.security.policy",
				PolicyFileLocator.getLocationOfPolicyFile());

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		doCustomRmiHandling();
	}

	/**
	 * extend this class and do RMI handling here
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 */
	public abstract void doCustomRmiHandling() throws MalformedURLException, RemoteException, NotBoundException;

}
