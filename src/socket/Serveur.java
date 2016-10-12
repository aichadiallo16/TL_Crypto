package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import tools.Equipement;

public class Serveur {
	public Serveur() {

		
		ServerSocket serverSocket  ;
		Socket NewServerSocket ;
		InputStream NativeIn = null;	
		ObjectInputStream ois = null;	
		OutputStream NativeOut = null;	
		ObjectOutputStream oos = null;
		
		
		
		try {
		
			serverSocket = new ServerSocket(8080);
			System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
			NewServerSocket = serverSocket.accept(); 
		        System.out.println("Un équipement s'est connecté");
		        
		        NativeIn = NewServerSocket.getInputStream();	
		    	ois = new ObjectInputStream(NativeIn);	
		    	NativeOut = NewServerSocket.getOutputStream();	
		    	oos = new ObjectOutputStream(NativeOut);	
		    	
		    	String res = (String) ois.readObject();
		    	System.out.println(res);
		    	
		    	oos.writeObject("Au revoir");	
		    	oos.flush();	
		    	
		    	ois.close();
		    	oos.close();
		    	NativeIn.close();
		    	NativeOut.close();
			
		                
		        NewServerSocket.close();
		        serverSocket.close();
		        
		}catch (IOException | ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	
	}
	
public void ajouteEquipement(Equipement equipement) {
	
}
}
