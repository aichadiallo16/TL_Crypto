package socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemReader;

import tools.Equipement;


public class Client {
	private static Equipement equipementClient;
	X509Certificate certifRecu;
	public Client(Equipement equipementClient) throws ClassNotFoundException, CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		Client.equipementClient = equipementClient;
		Socket clientSocket;
		InputStream NativeIn = null;
		ObjectInputStream ois = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;

		try {
			StringWriter sw = new StringWriter(); 
			JcaPEMWriter pw = new JcaPEMWriter(sw); 
			pw.writeObject(equipementClient.monCertif().x509); 
			pw.flush();
			pw.close();
			String pemCert = sw.toString();
			
			
			
			clientSocket = new Socket(InetAddress.getLocalHost(),equipementClient.getPort());	
		        System.out.println("Demande de connexion de" + equipementClient.monNom());
		        
		        NativeOut = clientSocket.getOutputStream();
		    	oos = new ObjectOutputStream(NativeOut);
		    	
		    	
		    	NativeIn = clientSocket.getInputStream();
		    	ois = new ObjectInputStream(NativeIn);
		    	
		    	/**
		    	 * le client envoye son certificat au serveur 
		    	 */
		    	oos.writeObject(pemCert);
		    	oos.flush();
		    	/**
		    	 * Apres la phase de connection, le client recoit le certificat du serveur et en verifie l'integrité
		    	 */
		    	String res1 = (String) ois.readObject();
		    	
		    	StringReader sr = new StringReader(res1);
		    	PemReader pr = new PemReader(sr);
		    	
		    	CertificateFactory cert = CertificateFactory.getInstance("X.509");
		    	ByteArrayInputStream in = (new ByteArrayInputStream(pr.readPemObject().getContent()));
		    	certifRecu = (X509Certificate) cert.generateCertificate(in);
		    	//on verifie le certificat recu avec la cle publique contenue dans 
		    	certifRecu.verify(certifRecu.getPublicKey());
		    	System.out.println(certifRecu.getIssuerDN() + "*****");
		    	equipementClient.addCa(certifRecu);
		    	//equipementClient.affichage_ca();
		    	pr.close();
		    	
		    	
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
	
	public static void main(String[] args) throws Exception {
		
		
		equipementClient = new Equipement("sonyClient", 5002);
		@SuppressWarnings("unused")
		Client client = new Client(equipementClient);
		
		
	}


}
