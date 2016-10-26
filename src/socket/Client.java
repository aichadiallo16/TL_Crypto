package socket;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;



import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.OperatorCreationException;
import tools.Certificat;
import tools.Equipement;


public class Client {
	private static Equipement equipementClient;
	X509Certificate certifRecu;
	public Client(Equipement equipementClient) throws ClassNotFoundException, CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalStateException, OperatorCreationException {
		Client.equipementClient = equipementClient;
		Socket clientSocket;
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
			pw.writeObject(equipementClient.monCertif().x509); 
			pw.flush();
			pw.close();
			String pemCert = sw.toString();


			//String IPServer = "192.168.56.101";
			clientSocket = new Socket(InetAddress.getLocalHost(),equipementClient.getPort());
			//clientSocket = new Socket(IPServer, equipementClient.getPort());
			System.out.println("Demande de connexion");
			
			///////////////////////////// Client recoit l autocertif du serveur ////////////////////////////////////////
			
			NativeIn1 = clientSocket.getInputStream();	
			ois1 = new ObjectInputStream(NativeIn1);
			
			NativeOut1 = clientSocket.getOutputStream();	
			oos1 = new ObjectOutputStream(NativeOut1);	

			
			String res1 = (String) ois1.readObject();
			
			////
			
			ois1.reset();
			String infoWifiServer = (String) ois1.readObject();
			System.out.println(infoWifiServer);

			X509Certificate certifRecu1 = certifFactory(res1);
			/*
			 * on verifie le certificat recu avec la cle publique contenue dans le certificat recu
			 */
			certifRecu1.verify(certifRecu1.getPublicKey());

			
			 ////////////////////// Envoi du certif signe avec la cle privee du Client pour la clee publique du Serveur /////////////
			 
			Certificat certifServeurparClient = new Certificat(equipementClient.monNom(), certifRecu1.getSubjectDN().toString().replaceAll("CN=", ""), certifRecu1.getPublicKey(), equipementClient.maClePrivee(), 10);

			StringWriter swback = new StringWriter(); 
			JcaPEMWriter pwback = new JcaPEMWriter(swback); 
			pwback.writeObject(certifServeurparClient.x509); 
			pwback.flush();
			pwback.close();
			String pemCertback = swback.toString();

			//System.out.println("Client: j'ai recu le certif:\n" + res1 + "\n+++++++++\n AutoCertif du Serveur decode: \n" +  certifRecu1
				//	+ "\n+++++++++\n Generation d'un certif pour le Serveur par le Client: \n" + certifServeurparClient.x509.toString());



			oos1.writeObject(pemCertback);
			oos1.flush();

			
			
			
			///////////////////////////////////////// Envoi de l autocertif du client au serveur ///////////////////////////////////////////

			NativeOut = clientSocket.getOutputStream();
			oos = new ObjectOutputStream(NativeOut);
			NativeIn = clientSocket.getInputStream();
			ois = new ObjectInputStream(NativeIn);
			oos.writeObject(pemCert);
			oos.flush();
			
			/////////////////////////// Reception du certif signe par le serveur et dont le sujet est le client ////////////////////////////

			String res = (String) ois.readObject();
			X509Certificate certifRecu = certifFactory(res);
			Boolean verif = certifRecu.getPublicKey().equals(equipementClient.maClePub());
			if (verif) {
				equipementClient.addCa(certifRecu);
			}
			
			//System.out.println("\nClient: j'ai recu un certificat sur ma cle publique signe par le serveur:\nVersion PEM:\n"
			//+ res + "\nVersion decode:\n" + certifRecu + "\nLe certificat recu a la bonne cle: " + verif);
			
			//System.out.println("\nAffichage CA:\n");
			//equipementClient.affichage_ca();

			ois.close();
			oos.close();
			NativeIn.close();
			NativeOut.close();
			ois1.close();
			oos1.close();
			NativeIn1.close();
			NativeOut1.close();
	
			
			///////////////////////////////////////////////// Fermeture Socket ///////////////////////////////////////////////////////

			clientSocket.close();
		       
		}catch (UnknownHostException e) {
			
			e.printStackTrace();
		}catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	
public X509Certificate certifFactory (String res) throws UnsupportedEncodingException, CertificateException, FileNotFoundException {
		
		CertificateFactory fact = CertificateFactory.getInstance("X.509");
	    InputStream is = new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
	    X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
	    
		return cer;
		
	}

	public static void main(String[] args) throws Exception {
		
		
		
		String nom = "DVDclient";
		
		equipementClient = new Equipement(nom, 5005);
		@SuppressWarnings("unused")
		Client client = new Client(equipementClient);
		
		
	}


}
