package tools;
/*package tools;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import socket.Client;
import socket.Serveur;

public class Main {
	static Equipement tvServer; 
	static Equipement dvdClient; 
	public static void main(String[] args) throws Exception {
		
		tvServer = new Equipement("samsungServer", 80);
		dvdClient = new Equipement("sonyClient", 80);
		System.out.println("\t\t\tEquipement " + tv.monNom() );
		System.out.println("Entrer : ");
		System.out.println("i --> Info sur l'esquipement ");
		System.out.println("q --> Quitter ");
		
		//Scanner scanner = new Scanner(System.in);
		//String readString = scanner.nextLine();
		*//**switch (readString) {
		case "i":
			tv.affichage();
		case "q":
			System.exit(0);
		
		}*//*
		
		 //generation de deux paires de clés pk1 et pk2
		
		PaireClesRSA pk1 = new PaireClesRSA();
		PaireClesRSA pk2 = new PaireClesRSA();
		
		Certificat certif = new Certificat("issuer", "subject", pk1, pk2, 10);
		
		//certification de la cle publique pk1 avec la cle privée de pk2
		
		certif.verifCertif(pk1.Publique());
		//System.out.println(certif.monCertif());
		
		startServer();
		startClient();
		
		
		
    	
	}
	
		public static void startServer() {
	        (new Thread() {
	            @Override
	            public void run() {
	            	try {
						Serveur serveur = new Serveur(tvServer);
					} catch (CertificateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchProviderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SignatureException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }).start();
		}
	     
		public static void startClient() {
	        (new Thread() {
	            @Override
	            public void run() {
	            	try {
						Client client = new Client(dvdClient);
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CertificateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchProviderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SignatureException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }).start();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		

	}


*/