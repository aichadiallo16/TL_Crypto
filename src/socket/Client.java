package socket;
/**
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Socket_client {
	int ServerPort;

	String ServerName;

	Socket clientSocket = null;

	InputStream NativeIn = null;

	ObjectInputStream ois = null;

	OutputStream NativeOut = null;

	ObjectOutputStream oos = null;

	// Creation de socket (TCP)

	try {

	clientSocket = new Socket(ServerName,ServerPort);

	} catch (Exception e) {

	// Gestion des exceptions

	}

	// Creation des flux natifs et evolues

	try {

	NativeOut = clientSocket.getOutputStream();

	oos = new ObjectOutputStream(NativeOut);

	NativeIn = clientSocket.getInputStream();

	ois = new ObjectInputStream(NativeIn);

	} catch (Exception e) {

	// Gestion des exceptions

	}

	// Emission d’un String

	try {

	oos.writeObject(“Bonjour“);

	oos.flush();

	} catch (Exception e) {

	// Gestion des exceptions

	}

	// Reception d’un String

	try {

	String res = (String) ois.readObject();

	System.out.println(res);

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
		clientSocket.close();

	} catch (IOException e) {

	// Gestion des exceptions
	}
	}
}
*/