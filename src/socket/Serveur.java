package socket;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Scanner;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import tools.Certificat;
import tools.Equipement;

public class Serveur {
	private static Equipement equipementServer;
	private static X509Certificate certifRecu;
	
	//private boolean activeClient;
	
	public Serveur(Equipement equipementServer) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalStateException, OperatorCreationException {

		Serveur.equipementServer = equipementServer;
		
		ServerSocket serverSocket  ;
		Socket NewServerSocket ;
		InputStream NativeIn = null;	
		ObjectInputStream ois = null;	
		OutputStream NativeOut = null;	
		ObjectOutputStream oos = null;
		InputStream NativeIn1 = null;	
		ObjectInputStream ois1 = null;	
		OutputStream NativeOut1 = null;	
		ObjectOutputStream oos1 = null;
		
		
		
		try {
			
			StringWriter sw = new StringWriter(); 
			JcaPEMWriter pw = new JcaPEMWriter(sw); 
			pw.writeObject(equipementServer.monCertif().x509); 
			pw.flush();
			pw.close();
			String pemCert = sw.toString();


			serverSocket = new ServerSocket(equipementServer.getPort());
			System.out.println("Le serveur est a l'ecoute du port "+ serverSocket.getLocalPort());
			NewServerSocket = serverSocket.accept(); 
			System.out.println("Un equipement s'est connecte");
			
			//////////////////////////////////////Envoi de l autocertif du serveur au client////////////////////////////////////////
			NativeOut1 = NewServerSocket.getOutputStream();	
			oos1 = new ObjectOutputStream(NativeOut1);
			NativeIn1 = NewServerSocket.getInputStream();	
			ois1 = new ObjectInputStream(NativeIn1);	
			System.out.println("outputstream cree");
			oos1.writeObject(pemCert);
			oos1.flush();
			
			//////////////////////// Reception du certif signe par le client et dont le sujet est le serveur ///////////////////////
			
			/**
			 * Apres la phase de connection, le serveur recoit le certificat emis sur sa cle publique par le client, et en verifie l'integrite
			 */

			String res1 = (String) ois1.readObject();
			X509Certificate certifRecu1 = certifFactory(res1);
			Boolean verif = certifRecu1.getPublicKey().equals(equipementServer.maClePub());
			if (verif) {
				equipementServer.addCa(certifRecu1);
			}

			System.out.println("\nAffichage CA:\n");
			equipementServer.affichage_ca();
			
			//ois1.close();
			//oos1.close();
			//NewServerSocket.shutdownInput();
			//NativeIn1.close();
			//NativeOut1.close();
			
			
			///////////////////////////// Serveur recoit l autocertif du client ////////////////////////////////////////
			
			NativeIn = NewServerSocket.getInputStream();	
			ois = new ObjectInputStream(NativeIn);	
			NativeOut = NewServerSocket.getOutputStream();	
			oos = new ObjectOutputStream(NativeOut);	

			String res = (String) ois.readObject();


			certifRecu = certifFactory(res);
			
			certifRecu.verify(certifRecu.getPublicKey());

			
			 ////////////////////// Envoi du certif signe avec la cle privee du serveur pour la clee publique du client /////////////
			 
			Certificat certifClientparServeur = new Certificat(equipementServer.monNom(), certifRecu.getSubjectDN().toString().replaceAll("CN=", ""), certifRecu.getPublicKey(), equipementServer.maClePrivee(), 10);

			StringWriter swback = new StringWriter(); 
			JcaPEMWriter pwback = new JcaPEMWriter(swback); 
			pwback.writeObject(certifClientparServeur.x509); 
			pwback.flush();
			pwback.close();
			String pemCertback = swback.toString();

			System.out.println("Serveur: j'ai recu le certif:\n" + res + "\n+++++++++\n AutoCertif du Client decode: \n" +  certifRecu
					+ "\n+++++++++\n Generation d'un certif pour le Client par le serveur: \n" + certifClientparServeur.x509.toString());



			oos.writeObject(pemCertback);
			oos.flush();


			ois1.close();
			oos1.close();
			NativeIn1.close();
			NativeOut1.close();
			ois.close();
			oos.close();
			NativeIn.close();
			NativeOut.close();
			
			///////////////////////////////////////////////// Fermeture Socket ///////////////////////////////////////////////////////

			NewServerSocket.close();
			serverSocket.close();
		}catch (IOException | ClassNotFoundException e) {
			
			e.printStackTrace();
		}
	
	}
	
public X509Certificate certifFactory (String res) throws UnsupportedEncodingException, CertificateException, FileNotFoundException {
		
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
	    InputStream is = new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
	    X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
	    
		return cer;
		
	}

public HashMap<String, Object> getInfoCertifRec() {
	HashMap<String, Object> info = new HashMap<String, Object>(); 
	
	info.put("Issuer", certifRecu.getIssuerDN().toString().replaceFirst("CN=", ""));
	info.put("PublicKey", certifRecu.getPublicKey());

	return info;
	
}


	
	public static void main(String[] args) throws Exception {
		
		equipementServer = new Equipement("samsungServer", 5002);
		
		Serveur serveur = new Serveur(equipementServer);
		String rep = "y";
		Scanner scanner = new Scanner(System.in);
		while(!rep.equals("n")) {
		System.out.println("Entrer : ");
		System.out.println("c --> Liste des equipements CA ");
		System.out.println("d --> Liste des equipement DA ");
		System.out.println("i --> Info sur l'equipement ");
		System.out.println("a --> Ajouter l'equipement " + serveur.getInfoCertifRec().get("Issuer"));
		System.out.println("q --> Quitter ");
		
		
		String readString = scanner.nextLine();
		
		
		
		if (readString.equals("i") && rep.equals("y") ) {
			
			System.out.println("Nom de l'equipement: " + serveur.getInfoCertifRec().get("Issuer"));
			System.out.println("Clé publique de l'equipement: " + serveur.getInfoCertifRec().get("PublicKey"));
			System.out.println("vous voulez faire autre chose? Y/N");
			Scanner reponseScan = new Scanner(System.in);
			rep = reponseScan.nextLine();
			if(rep.equals("n")) {
				reponseScan.close(); 
				
			}
		}
		
		else if (readString.equals("q") && rep.equals("y") ) {
			scanner.close();
			System.exit(0);
		}
			
		else if (readString.equals("a") && rep.equals("y") ) {
				equipementServer.addDa(Serveur.certifRecu);
				System.out.println("Equipement " + serveur.getInfoCertifRec().get("Issuer") + " ajouté\n");
					equipementServer.affichage_da();
			System.out.println("vous voulez faire autre chose? Y/N");
			Scanner reponseScan = new Scanner(System.in);
			rep = reponseScan.nextLine();
		
			if(rep.equals("n")) {
				reponseScan.close(); 
				
			}
		}
		else if (readString.equals("c") && rep.equals("y") ) {
			equipementServer.affichage_ca();
			
			Scanner reponseScan = new Scanner(System.in);
			rep = reponseScan.nextLine();
			if(rep.equals("n")) {
				reponseScan.close(); 
				
			}
		}
		else if (readString.equals("d") && rep.equals("y") ) {
			equipementServer.affichage_da();
			//System.out.println("D");
			Scanner reponseScan = new Scanner(System.in);
			rep = reponseScan.nextLine();
			if(rep.equals("n")) {
				reponseScan.close(); 
				
			}
		}

	}	
		
	scanner.close();
		
	}

	
	

}
