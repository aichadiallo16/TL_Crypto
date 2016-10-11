package socket;
/**
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Socket_serveur {
	ServerSocket serverSocket = null;
	
	Socket NewServerSocket = null;
	
	InputStream NativeIn = null;
	
	ObjectInputStream ois = null;
	
	OutputStream NativeOut = null;
	
	ObjectOutputStream oos = null;
	
	// Creation de socket (TCP)
	
	try {
	
	serverSocket = new ServerSocket(this.monPort);
	
	} catch (IOException e) {
	
	// Gestion des exceptions
	
	}
	
	// Attente de connextions
	
	try {
	
	NewServerSocket = serverSocket.accept();
	
	} catch (Exception e) {
	
	// Gestion des exceptions
	
	}
	
	// Creation des flux natifs et evolues
	
	try {
	
	NativeIn = NewServerSocket.getInputStream();
	
	ois = new ObjectInputStream(NativeIn);
	
	NativeOut = NewServerSocket.getOutputStream;
	
	oos = new ObjectOutputStream(NativeOut);
	
	} catch (IOException e) {
	
	// Gestion des exceptions
	
	}
	
	// Reception d’un String
	
	try {
	
	String res = (String) ois.readObject();
	
	System.out.println(res);
	
	} catch (Exception e) {
	
	// Gestion des exceptions
	
	}
	
	// Emission d’un String
	
	try {
	
	oos.writeObject(“Au revoir“);
	
	oos.flush();
	
	} catch (Exception e) {
	
	// Gestion des exceptions
	
	}
	
	// Fermeture des flux evolues et natifs
	
	try {
	
	ois.close();
	oos.close();

	NativeIn.close();

	NativeOut.close();

	} catch (IOException e) {

	// Gestion des exceptions

	}

	// Fermeture de la connexion

	try {

	NewServerSocket.close();

	} catch (IOException e) {

	// Gestion des exceptions

	}

	// Arret du serveur

	try {

	serverSocket.close();

	} catch (IOException e) {

	// Gestion des exceptions

	}
}
*/