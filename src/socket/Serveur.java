package socket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Scanner;

import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import tools.Certificat;
import tools.Equipement;

public class Serveur {
	private static Equipement equipementServer;
	public Serveur(Equipement equipementServer) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

		this.equipementServer = equipementServer;
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
			
			
			serverSocket = new ServerSocket(equipementServer.getPort());
			System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
			NewServerSocket = serverSocket.accept(); 
		        System.out.println("Un équipement s'est connecté");
		        
		        
		        NativeIn = NewServerSocket.getInputStream();	
		    	ois = new ObjectInputStream(NativeIn);	
		    	NativeOut = NewServerSocket.getOutputStream();	
		    	oos = new ObjectOutputStream(NativeOut);	
		    	
		    	String res = (String) ois.readObject();
		    	
		    	StringReader sr = new StringReader(res);
		    	
		    	PemReader pr = new PemReader(sr);
		    	
		    	CertificateFactory cert = CertificateFactory.getInstance("X.509");
		    	
		    	byte[] bytes = pr.readPemObject().getContent();
		    	ByteArrayInputStream in = (new ByteArrayInputStream(bytes));
		    	X509Certificate certifRecu = (X509Certificate) cert.generateCertificate(in);
		    	//on verifie le certificat recu avec la cle publique contenue dans 
		    	certifRecu.verify(certifRecu.getPublicKey());
		    	
		    	System.out.println(certifRecu.getIssuerDN() + "_____");
		    	pr.close();
		    	
		    	oos.writeObject(pemCert);
		    	oos.flush();
		    	/*oos.writeObject("Au revoir");	
		    	
		    	oos.flush();	*/
		    	
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
	public static void main(String[] args) throws Exception {
		
		equipementServer = new Equipement("samsungServer", 8080);
		Serveur serveur = new Serveur(equipementServer);
		System.out.println("Entrer : ");
		System.out.println("i --> Info sur l'esquipement ");
		System.out.println("a --> Ajouter l'equipement ");
		System.out.println("q --> Quitter ");
		
		Scanner scanner = new Scanner(System.in);
		String readString = scanner.nextLine();
		switch (readString) {
		case "i":
			equipementServer.affichage();
		case "q":
			System.exit(0);
		
		}
		
	}

	
	

}
