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
		
		
		
		
		try {
			
			StringWriter sw = new StringWriter(); 
			JcaPEMWriter pw = new JcaPEMWriter(sw); 
			pw.writeObject(equipementServer.monCertif().x509); 
			pw.flush();
			pw.close();
			String pemCert = sw.toString();
			
			System.out.println("\t\t___________" + equipementServer.monNom() + "___________"); 
			serverSocket = new ServerSocket(equipementServer.getPort());
			//System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
			NewServerSocket = serverSocket.accept(); 
		        
		        
		        
		        NativeIn = NewServerSocket.getInputStream();	
		    	ois = new ObjectInputStream(NativeIn);	
		    	NativeOut = NewServerSocket.getOutputStream();	
		    	oos = new ObjectOutputStream(NativeOut);	
		    	
		    	String res = (String) ois.readObject();
		    	

		    	certifRecu = certifFactory(res);
		    	/**
		    	 * on verifie le autocertificat recu avec la cle publique contenue dans le certificat recu
		    	 */
		    	certifRecu.verify(certifRecu.getPublicKey());
		    	System.out.println("Un équipement s'est connecté: " + getInfoCertifRec().get("Issuer"));
		    	
		    	/**
		    	 * Le server crée le certificat signé avec sa cle privee pour la clee publique di client
		    	 */
		    	@SuppressWarnings("unused")
				Certificat certifClientparServeur = new Certificat(equipementServer.monNom(), certifRecu.getSubjectDN().toString(), certifRecu.getPublicKey(), equipementServer.maClePrivee(), 10);
		    	
		    	/**
		    	 * il faut envoyer certifClientparServeur au client
		    	 */
		    	
		    	
		
		    	oos.writeObject(pemCert);
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
