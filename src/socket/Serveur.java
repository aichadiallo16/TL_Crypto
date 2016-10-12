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
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import tools.Certificat;
import tools.Equipement;

public class Serveur {
	Equipement equipementServer;
	public Serveur(Equipement equipementServer) throws CertificateException {

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
			
			
			serverSocket = new ServerSocket(5002);
			System.out.println("Le serveur est à l'écoute du port "+ serverSocket.getLocalPort());
			NewServerSocket = serverSocket.accept(); 
		        System.out.println("Un équipement s'est connecté");
		        //System.out.println(equipementServer.monCertif().x509.toString() + "\n");
		        
		        NativeIn = NewServerSocket.getInputStream();	
		    	ois = new ObjectInputStream(NativeIn);	
		    	NativeOut = NewServerSocket.getOutputStream();	
		    	oos = new ObjectOutputStream(NativeOut);	
		    	
		    	String res = (String) ois.readObject();
		    	
		    	
		    	StringReader sr = new StringReader(pemCert);
		    	PemReader pr = new PemReader(sr);
		    	X509Certificate certifRecu = certifFactory(pemCert);
		    	pr.close();
		    	
		    	System.out.println(certifRecu.getPublicKey().toString() + "+++++++++");
		
		    	oos.writeObject(pemCert);
		    	oos.flush();
		    	oos.writeObject("Au revoir");	
		    	
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

	public X509Certificate certifFactory (String pr) throws UnsupportedEncodingException, CertificateException {
		
		String clean = pr.toString().replaceAll("-----BEGIN CERTIFICATE-----", "").replaceAll("-----END CERTIFICATE-----", "");
		byte[] bytes = clean.getBytes("UTF-8");
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		CertificateFactory cert = CertificateFactory.getInstance("X.509");
		X509Certificate certifRecu = (X509Certificate) cert.generateCertificate(in);
		
		return certifRecu;
		
	}
	

}
