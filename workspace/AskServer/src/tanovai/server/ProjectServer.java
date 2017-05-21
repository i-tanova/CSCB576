package tanovai.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class ProjectServer {
 private Queue <Socket> connections = new LinkedList<Socket>();
 private static boolean isStopped = false;
 
  private static ProjectServer server;
 
 public ProjectServer(int port){
		try {
			startServer(port);
		} catch (IOException e) {
			e.printStackTrace();
		} 
 }
 
 
 public void startServer(int port) throws IOException{
	 ServerSocket servSocket = new ServerSocket(port);
	 isStopped = false;
	 System.out.println("Server started..");
	 Socket socket = null;
	 while(!isStopped){
		 if(connections.isEmpty()){
			try {
					socket = servSocket.accept();
				new ThreadWorker(connections, socket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
		 } 
	 }
	 try {
		socket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 try {
		servSocket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
 }
 

 public static void stopServer(){
	 isStopped = true;
 }
 
 public static void startNewServer(int port){
	 if(server != null){
		 server.stopServer();
	 }
	 if(server == null){
		 server = new ProjectServer(port);
	 }
 }
}
