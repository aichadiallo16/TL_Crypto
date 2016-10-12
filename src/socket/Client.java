package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {
	
	public Client() throws ClassNotFoundException {
		
		
		Socket clientSocket;
		InputStream NativeIn = null;
		ObjectInputStream ois = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;
		
		
		

		try {
		
			clientSocket = new Socket(InetAddress.getLocalHost(),8080);	
		        System.out.println("Demande de connexion");

		       
		       
		        NativeOut = clientSocket.getOutputStream();
		    	oos = new ObjectOutputStream(NativeOut);
		    	NativeIn = clientSocket.getInputStream();
		    	ois = new ObjectInputStream(NativeIn);
		    	oos.writeObject("Bonjour equipement");
		    	oos.flush();
		    	String res = (String) ois.readObject();
		    	System.out.println(res);
		    	ois.close();
		    	oos.close();
		    	NativeIn.close();
		    	NativeOut.close();
		        clientSocket.close();
		       
		}catch (UnknownHostException e) {
			
			e.printStackTrace();
		}catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
