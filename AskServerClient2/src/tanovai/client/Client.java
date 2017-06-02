package tanovai.client;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
private OutputStreamWriter osw;
private BufferedReader in;
	/**
	 * Connects to server and return Socket to write to
	 * 
	 * @param host
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public Socket connectToServer(String host, int port) throws IOException {
		System.out.println("AskClient is connecting..");
		grantPolicy();
		/** Obtain an address object of the server */
		InetAddress address = InetAddress.getByName(host);
		/** Establish a socket connetion */
		Socket connection = new Socket(address, port);
		return connection;
	}
	
	public static void grantPolicy(){
		System.setProperty("java.rmi.server.codebase",
				Client.class.getProtectionDomain()
						.getCodeSource().getLocation().toString());

		System.setProperty("java.security.policy",
				System.getProperty("user.dir")+ "/Resources/rmi.policy");

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}

	public void writeToSocket(Socket connection, String message)
			throws IOException {
		if(osw == null){
			System.out.println("Client getting output");
		BufferedOutputStream bos = new BufferedOutputStream(
				connection.getOutputStream());

		/**
		 * Instantiate an OutputStreamWriter object with the optional character
		 * encoding.
		 */
		osw = new OutputStreamWriter(bos, "UTF-8");
		}
		System.out.println("Clent will write request: " + message);
		osw.write(message);
		osw.flush();		
	}

	public String readFromSocket(Socket connection) throws IOException {
		if(in == null){
		  in = new BufferedReader(new
		            InputStreamReader(connection.getInputStream()));
		  System.out.println("Client getting input");
		}   
                 String inputLine = null;
                 StringBuffer response = new StringBuffer();
		         while ((inputLine = in.readLine()) != null) {
		        	 System.out.print("Received string: " + inputLine);
		             response.append(inputLine);
		             if (inputLine.equals("BYE"))
		                 break;
		         }
		return response.toString();
	}
	
	public void closeConnection(Socket connection){
		 System.out.println("Ask Client is closing connection..");
			/** Close the socket connection. */
		 try {
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			in = null;
		}
		 try {
			osw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			osw = null;
		}
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Ask Client closed connection");
	}

}
