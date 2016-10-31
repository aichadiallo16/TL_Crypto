package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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

import java.util.Map.Entry;
import org.bouncycastle.operator.OperatorCreationException;
import tools.Certificat;
import tools.Equipement;

public class Serveur {
	private static Equipement equipementServer;
	private static Socket NewServerSocket;

	@SuppressWarnings("resource")
	public Serveur(Equipement equipementServer)
			throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException, IllegalStateException, OperatorCreationException, IOException, ClassNotFoundException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		Serveur.equipementServer = equipementServer;
		ServerSocket serverSocket;
		serverSocket = new ServerSocket(equipementServer.getPort());
		NewServerSocket = serverSocket.accept();
		equipementServer.addDa(equipementServer.monNom());
		equipementServer.addCa(equipementServer.monCertif().x509);

	}

	public String connexion() // echange des noms des equipements et des infos reseau
			throws IOException, ClassNotFoundException { 

		InputStream NativeIn = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;
		InputStream NativeIn1 = null;
		ObjectInputStream ois1 = null;
		OutputStream NativeOut1 = null;
		ObjectOutputStream oos1 = null;

		// Envoi de l'action a effectuer au client (ici autoriser la connexion (a))

		int choix = 1;

		NativeOut = NewServerSocket.getOutputStream();
		oos = new ObjectOutputStream(NativeOut);
		NativeIn = NewServerSocket.getInputStream();
		new ObjectInputStream(NativeIn);

		oos.writeObject(choix);
		oos.flush();

		// Envoi du nom du serveur au client

		NativeOut1 = NewServerSocket.getOutputStream();
		oos1 = new ObjectOutputStream(NativeOut1);
		NativeIn1 = NewServerSocket.getInputStream();
		ois1 = new ObjectInputStream(NativeIn1);

		oos1.writeObject(equipementServer.monNom());
		oos1.flush();

		// Reception des infos client (nom + reseau)

		String infoClient = (String) ois1.readObject();
		return infoClient;
	}

	public void synchronisation() 
			throws IOException, CertificateException, ClassNotFoundException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalStateException,
			OperatorCreationException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
	
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

		// Envoi de l'action a effectuer au client (ici synchronisation (s))
		
		int choix = 2;
		NativeOut2 = NewServerSocket.getOutputStream();
		oos2 = new ObjectOutputStream(NativeOut2);
		NativeIn2 = NewServerSocket.getInputStream();
		new ObjectInputStream(NativeIn2);

		oos2.writeObject(choix);
		oos2.flush();

		// Le serveur recoit l autocertificat du client et le verifie

		NativeIn = NewServerSocket.getInputStream();
		ois = new ObjectInputStream(NativeIn);
		NativeOut = NewServerSocket.getOutputStream();
		oos = new ObjectOutputStream(NativeOut);

		X509Certificate autoCertifertifRecu;
		autoCertifertifRecu = equipementServer.certifFactory(ois);
		autoCertifertifRecu.verify(autoCertifertifRecu.getPublicKey()); // verification

		// Le serveur envoie son autocertificat

		equipementServer.envoiCertif(equipementServer.monCertif().x509, oos);
		oos.flush();
		
		// Reception du certificat portant sur la cle publique du serveur et signe par le client

		NativeIn1 = NewServerSocket.getInputStream();
		ois1 = new ObjectInputStream(NativeIn1);
		NativeOut1 = NewServerSocket.getOutputStream();
		oos1 = new ObjectOutputStream(NativeOut1);

		X509Certificate certifRecu = equipementServer.certifFactory(ois1);
		Boolean verif = certifRecu.getPublicKey().equals(equipementServer.maClePub()); // on verifie que le certificat porte sur la cle publique du serveur
		if (verif) {
			equipementServer.addCa(certifRecu);
		}

		// Envoi du certif signe avec la cle privee du serveur pour la clee publique du client

		Certificat certifClientparServeur = new Certificat(equipementServer.monNom(),
				autoCertifertifRecu.getSubjectDN().toString().replaceAll("CN=", ""), autoCertifertifRecu.getPublicKey(),
				equipementServer.maClePrivee(), 10);

		equipementServer.envoiCertif(certifClientparServeur.x509, oos1);

		// Reception de DAclient et ajout de DAclient dans DAserver

		NativeIn3 = NewServerSocket.getInputStream();
		ois3 = new ObjectInputStream(NativeIn3);
		NativeOut3 = NewServerSocket.getOutputStream();
		oos3 = new ObjectOutputStream(NativeOut3);

		equipementServer.putAllDA(ois3, equipementServer);

		// Envoi de DAserver au client

		oos3.writeObject(equipementServer.equipDa());
		oos3.flush();

		// Reception de CAclient
		InputStream NativeIn4 = null;
		ObjectInputStream ois4 = null;
		OutputStream NativeOut4 = null;
		NativeIn4 = NewServerSocket.getInputStream();
		ois4 = new ObjectInputStream(NativeIn4);
		NativeOut4 = NewServerSocket.getOutputStream();
		new ObjectOutputStream(NativeOut4);

		int tailleC = (int) ois4.readObject();

		while (tailleC != 0) {

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

		// Envoi de CAserver au client

		Iterator<Entry<String, X509Certificate>> it = equipementServer.certifCa().entrySet().iterator();
		int tailleS = equipementServer.certifCa().size();
		InputStream NativeIn6 = null;
		OutputStream NativeOut6 = null;
		ObjectOutputStream oos6 = null;
		NativeOut6 = NewServerSocket.getOutputStream();
		oos6 = new ObjectOutputStream(NativeOut6);
		NativeIn6 = NewServerSocket.getInputStream();
		new ObjectInputStream(NativeIn6);

		oos6.writeObject(tailleS); // le serveur envoie d'abord le nombre de certificats qu'il va envoyer

		while (it.hasNext()) {
			InputStream NativeIn7 = null;
			OutputStream NativeOut7 = null;
			ObjectOutputStream oos7 = null;

			NativeOut7 = NewServerSocket.getOutputStream();
			oos7 = new ObjectOutputStream(NativeOut7);
			NativeIn7 = NewServerSocket.getInputStream();
			new ObjectInputStream(NativeIn7);

			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();
			equipementServer.envoiCertif((X509Certificate) pair.getValue(), oos7);

		}

	}

	public String[] splitString(String res) {
		String[] result = res.split("_%_");
		return result;

	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		equipementServer = new Equipement("samsungServer", 6000);
		Serveur serveur = new Serveur(equipementServer);
		String rep = "";
		Scanner scanner = new Scanner(System.in);

		while (!rep.equals("n")) {
			System.out.println("Entrer : ");
			System.out.println("i --> Info sur l'equipement "+ equipementServer.monNom());
			System.out.println("d --> Liste des equipements de " + equipementServer.monNom() + " (DA) ");
			System.out.println("c --> Liste des certificats de " + equipementServer.monNom() + " (CA) ");
			System.out.println("a --> Autoriser la connection avec " + serveur.splitString(serveur.connexion())[0]);
			System.out.println("s --> Synchronisation avec " + serveur.splitString(serveur.connexion())[0]);
			System.out.println("q --> Quitter ");

			String readString = scanner.nextLine();
			if (readString.equals("i")) {
				System.out.println("Nom de l'equipement: " + equipementServer.monNom());
				System.out.println("Certificat de l'equipement: " + equipementServer.afficheMonCertif());
				rep = "";
			}

			else if (readString.equals("q")) {
				System.out.println("Fin de la connexion.");
				scanner.close();
				System.exit(0);
			}

			else if (readString.equals("d")) {
				equipementServer.affichage_da();
				rep = "";
			}

			else if (readString.equals("a")) {
				String[] output = serveur.splitString(serveur.connexion());

				if (equipementServer.verifyNetwork(output[1])) {

					equipementServer.addDa(output[0]);
					System.out.println("L'equipement " + output[0] + " a ete correctement ajoute");
					rep = "";
				}

				else {
					System.out.println("L'equipement " + output[0] + " n'appartient pas a votre reseau");
				}
			}

			else if (readString.equals("c")) {
				equipementServer.affichage_ca();
				rep = "";
				
			}
			
			else if (readString.equals("s")) {
				String[] output = serveur.splitString(serveur.connexion()); 
				
				if (equipementServer.equipDa().containsValue(output[0])) { // si le client n'est pas dans DA on ne peut pas synchroniser les equipements
					serveur.synchronisation();
					
				} else {
					System.out.println("Equipement inconnu. La connection doit etre autorisee. (a)");
				}
				rep = "";
			} 
			
			else {
				System.out.println("Mauvaise touche. ");
				rep = "";
			}

			while (!rep.equals("y")) {
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