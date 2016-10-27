package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import java.util.Map.Entry;
import org.bouncycastle.operator.OperatorCreationException;
import tools.Certificat;
import tools.Equipement;

public class Serveur {
	private static Equipement equipementServer;
	private static X509Certificate certifRecu;
	private static Socket NewServerSocket;
	
    
	

	@SuppressWarnings("resource")
	public Serveur(Equipement equipementServer)
			throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException, IllegalStateException, OperatorCreationException, IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Serveur.equipementServer = equipementServer;
		ServerSocket serverSocket  ;
		equipementServer.addDa(equipementServer.monNom());
		equipementServer.addCa(equipementServer.monCertif().x509);
		
		serverSocket = new ServerSocket(equipementServer.getPort());
    		NewServerSocket = serverSocket.accept();
	
	}
	
	
	
	public String clientName() throws IOException, ClassNotFoundException {
		/*OutputStream NativeOutWifi = null;
		ObjectOutputStream oosWifi = null;
		InputStream NativeInWifi = null;
		ObjectInputStream oisWifi = null;
		NativeInWifi = NewServerSocket.getInputStream();
		oisWifi = new ObjectInputStream(NativeInWifi);
		NativeOutWifi = NewServerSocket.getOutputStream();
		oosWifi = new ObjectOutputStream(NativeOutWifi); 
		
		////////////////////////Reception des 
		//////////////////////// infos Wifi du client
		
		
		String info = (String) oisWifi.readObject();
		System.out.println(info);
		equipementServer.addInfoClientWifi("ciai");*/
		
		//////////////////////////////////////////// Envoi du nom du serveur
		//////////////////////////////////////////// au client
		int choix = 1 ;
		InputStream NativeIn = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;
		InputStream NativeIn1 = null;
		ObjectInputStream ois1 = null;
		OutputStream NativeOut1 = null;
		ObjectOutputStream oos1 = null;
		NativeOut = NewServerSocket.getOutputStream();
		oos = new ObjectOutputStream(NativeOut);
		NativeIn = NewServerSocket.getInputStream();
		new ObjectInputStream(NativeIn);

		oos.writeObject(choix);
		oos.flush();
		
		NativeOut1 = NewServerSocket.getOutputStream();
		oos1 = new ObjectOutputStream(NativeOut1);
		NativeIn1 = NewServerSocket.getInputStream();
		ois1 = new ObjectInputStream(NativeIn1);
		
		oos1.writeObject(equipementServer.monNom());
		oos1.flush();

		//////////////////////////////////////////////// Ajout du client
		//////////////////////////////////////////////// dans DA

		String res2 = (String) ois1.readObject();
		return res2;
	}


	
	public void initServer() throws IOException, CertificateException, ClassNotFoundException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalStateException,
			OperatorCreationException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		int choix = 2 ;
		InputStream NativeIn = null;
		ObjectInputStream ois = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;
		InputStream NativeIn1 = null;
		ObjectInputStream ois1 = null;
		OutputStream NativeOut1 = null;
		ObjectOutputStream oos1 = null;
		InputStream NativeIn2 = null;
		OutputStream NativeOut2 = null;
		ObjectOutputStream oos2 = null;
		InputStream NativeIn3 = null;
		ObjectInputStream ois3 = null;
		OutputStream NativeOut3 = null;
		ObjectOutputStream oos3 = null;
		

		NativeOut2 = NewServerSocket.getOutputStream();
		oos2 = new ObjectOutputStream(NativeOut2);
		NativeIn2 = NewServerSocket.getInputStream();
		new ObjectInputStream(NativeIn2);
		
		oos2.writeObject(choix);
		oos2.flush();

		///////////////////////////// Serveur recoit l autocertif du client
		///////////////////////////// ////////////////////////////////////////

		NativeIn = NewServerSocket.getInputStream();
		ois = new ObjectInputStream(NativeIn);
		NativeOut = NewServerSocket.getOutputStream();
		oos = new ObjectOutputStream(NativeOut);

		certifRecu = equipementServer.certifFactory(ois);
		certifRecu.verify(certifRecu.getPublicKey());

		//////////////////////////////Serveur envoie son autocertif

		equipementServer.envoiCertif(equipementServer.monCertif().x509, oos);
		oos.flush();
		//////////////////////////Reception du certif signe par le client
				
		NativeIn1 = NewServerSocket.getInputStream();
		ois1 = new ObjectInputStream(NativeIn1);
		NativeOut1 = NewServerSocket.getOutputStream();
		oos1 = new ObjectOutputStream(NativeOut1);
		
		X509Certificate certifRecu1 = equipementServer.certifFactory(ois1);
		Boolean verif = certifRecu1.getPublicKey().equals(equipementServer.maClePub());
		if (verif) {
		equipementServer.addCa(certifRecu1);
		}
		
		////////////////////// Envoi du certif signe avec la cle privee du
		////////////////////// serveur pour la clee publique du client
		
		Certificat certifClientparServeur = new Certificat(equipementServer.monNom(),
				certifRecu.getSubjectDN().toString().replaceAll("CN=", ""), certifRecu.getPublicKey(),
				equipementServer.maClePrivee(), 10);

		equipementServer.envoiCertif(certifClientparServeur.x509, oos1);
		
		//////////////////////// Reception de DAclient et ajout
		//////////////////////// de DAclient dans DAserver
		
		NativeIn3 = NewServerSocket.getInputStream();
		ois3 = new ObjectInputStream(NativeIn3);
		NativeOut3 = NewServerSocket.getOutputStream();
		oos3 = new ObjectOutputStream(NativeOut3);
		
		equipementServer.putAllDA(ois3, equipementServer);
		
		
		///////////////////////// Envoi de DAserver au
		////////////////////////// client
	
		oos3.writeObject(equipementServer.equipDa());
		oos3.flush();
		
		//////////////////////// Reception de CAclient
		InputStream NativeIn4 = null;
		ObjectInputStream ois4 = null;
		OutputStream NativeOut4 = null;
		NativeIn4 = NewServerSocket.getInputStream();
		ois4 = new ObjectInputStream(NativeIn4);
		NativeOut4 = NewServerSocket.getOutputStream();
		new ObjectOutputStream(NativeOut4);
		
		int tailleC = (int) ois4.readObject();;
		
		while(tailleC != 0) {
			
			InputStream NativeIn5 = null;
			ObjectInputStream ois5 = null;
			OutputStream NativeOut5 = null;
			NativeIn5 = NewServerSocket.getInputStream();
			ois5 = new ObjectInputStream(NativeIn5);
			NativeOut5 = NewServerSocket.getOutputStream();
			new ObjectOutputStream(NativeOut5);
			
			X509Certificate certifCa = equipementServer.certifFactory(ois5);
			equipementServer.addCa(certifCa);
			tailleC--;
		}
		
		/////////////////////////// Envoi de CAserver au client
				
		Iterator<Entry<String, X509Certificate>> it = equipementServer.certifCa().entrySet().iterator();
		int tailleS = equipementServer.certifCa().size();
		InputStream NativeIn6 = null;
		OutputStream NativeOut6 = null;
		ObjectOutputStream oos6 = null;
		
		NativeOut6 = NewServerSocket.getOutputStream();
		oos6 = new ObjectOutputStream(NativeOut6);
		NativeIn6 = NewServerSocket.getInputStream();
		new ObjectInputStream(NativeIn6);
		
		oos6.writeObject(tailleS);
		
		while (it.hasNext()) {
				InputStream NativeIn7 = null;
				OutputStream NativeOut7 = null;
				ObjectOutputStream oos7 = null;
				
				NativeOut7 = NewServerSocket.getOutputStream();
				oos7 = new ObjectOutputStream(NativeOut7);
				NativeIn7 = NewServerSocket.getInputStream();
				new ObjectInputStream(NativeIn7);
			
		    	@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry)it.next();
		    	equipementServer.envoiCertif((X509Certificate) pair.getValue(), oos7);
		       
		    }
		
		
	}

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		equipementServer = new Equipement("samsungServer", 6003);

		Serveur serveur = new Serveur(equipementServer);
		String rep = "";
		
		Scanner scanner = new Scanner(System.in);
		
		while (!rep.equals("n")) {
			System.out.println("Entrer : ");
			System.out.println("i --> Info sur l'equipement ");
			System.out.println("d --> Liste des equipements de " + equipementServer.monNom() + " (DA) ");
			System.out.println("c --> Liste des certificats de " + equipementServer.monNom() + " (CA) ");
			System.out.println("a --> Autoriser la connection avec l'equipement client");
			System.out.println("s --> Initialisation (en tant que serveur) ");
			System.out.println("q --> Quitter ");

			String readString = scanner.nextLine();
			if (readString.equals("i")) 
			{
				System.out.println("Nom de l'equipement: " + equipementServer.monNom());
				System.out.println("Certificat de l'equipement: " + equipementServer.afficheMonCertif());
				rep= "";
			}

			else if (readString.equals("q")) 
			{
				System.out.println("Fin de la connexion.");
				scanner.close();
				System.exit(0);
			}

			else if (readString.equals("d")) 
			{
				equipementServer.affichage_da();
				rep= "";
			}
			
			else if (readString.equals("a")) 
			{
				//if (equipementServer.verifyNetwork(infoWifiClient)) {
				equipementServer.addDa(serveur.clientName());
				rep= "";
				//}
				//else {
					System.out.println("l'equipement n'appartient pas a votre reseau" + equipementServer.getInfoClientWifi() );
				//}
			}

			else if (readString.equals("c")) 
			{
				equipementServer.affichage_ca();
				rep= "";
			}  
			else if (readString.equals("s")) 
			{																			// Cette condition n'est pas une securite, elle force juste l'utilisateur  
				if(equipementServer.equipDa().containsValue(serveur.clientName())){    //a ajouter l'equipement  client dans DA avant d'echanger les certif
					serveur.initServer();
				}
				else {
					System.out.println("Equipement inconnu. La connection doit etre autorisee. (a)");
				}
				rep= "";
			}
			else 
			{
				System.out.println("Mauvaise touche. ");	
				rep= "";
			}
			
			while(!rep.equals("y")){
				System.out.println("Vous voulez faire autre chose? y/n");
				
				Scanner reponseScan = new Scanner(System.in);
				rep = reponseScan.nextLine();
				if (rep.equals("n")) {
					System.out.println("Fin de la connexion.");
					scanner.close();	
					System.exit(0);
				}
				
			}
			
		}
		scanner.close();
		
		NewServerSocket.close();
	}

}