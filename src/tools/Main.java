package tools;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.util.Scanner;

import socket.Client;
import socket.Serveur;

public class Main {
	static Equipement tvServer; 
	static Equipement dvdClient; 
	public static void main(String[] args) throws Exception {
		
		tvServer = new Equipement("samsungServer", 80);
		dvdClient = new Equipement("sonyClient", 80);
		/*System.out.println("\t\t\tEquipement " + tv.monNom() );
		System.out.println("Entrer : ");
		System.out.println("i --> Info sur l'esquipement ");
		System.out.println("q --> Quitter ");*/
		
		//Scanner scanner = new Scanner(System.in);
		//String readString = scanner.nextLine();
		/**switch (readString) {
		case "i":
			tv.affichage();
		case "q":
			System.exit(0);
		
		}*/
		
		 //generation de deux paires de clés pk1 et pk2
		
		PaireClesRSA pk1 = new PaireClesRSA();
		PaireClesRSA pk2 = new PaireClesRSA();
		
		Certificat certif = new Certificat("issuer", "subject", pk1, pk2, 10);
		
		//certification de la cle publique pk1 avec la cle privée de pk2
		
		certif.verifCertif(pk1.Publique());
		System.out.println(certif.monCertif());
		
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
					}
	            }
	        }).start();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		

	}


