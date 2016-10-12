package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

import tools.Equipement;


public class Client {
	Equipement equipementClient;
	public Client(Equipement equipementClient) throws ClassNotFoundException {
		this.equipementClient = equipementClient;
		Socket clientSocket;
		InputStream NativeIn = null;
		ObjectInputStream ois = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;
		
		
		

		try {
		
			clientSocket = new Socket(InetAddress.getLocalHost(),5002);	
		        System.out.println("Demande de connexion");
		        //System.out.println("Nom du client:" + equipementClient.monNom());

		       
		       
		        NativeOut = clientSocket.getOutputStream();
		    	oos = new ObjectOutputStream(NativeOut);
		    	
		    	
		    	NativeIn = clientSocket.getInputStream();
		    	ois = new ObjectInputStream(NativeIn);
		    	//oos.writeObject("Bonjour server\n");
		    	//oos.flush();
		    	
		    	//oos.writeObject("Voici mon nom, server: " + equipementClient.monNom() + "\n");
		    	//oos.flush();
		    	oos.writeObject(equipementClient.monCertif().x509.toString());
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
