package socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.bouncycastle.operator.OperatorCreationException;
import tools.Certificat;
import tools.Equipement;
import tools.PaireClesRSA;

public class Client {
	private static Equipement equipementClient;
	X509Certificate certifRecu;
	private Socket clientSocket;

	public Client(Equipement equipementClient)
			throws ClassNotFoundException, CertificateException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException, IllegalStateException, OperatorCreationException, IOException {

		Client.equipementClient = equipementClient;
		equipementClient.addDa(equipementClient.monNom());
		equipementClient.addCa(equipementClient.monCertif().x509);

		InputStream NativeIn = null;
		ObjectInputStream ois = null;
		OutputStream NativeOut = null;
		ObjectOutputStream oos = null;
		InputStream NativeIn1 = null;
		ObjectInputStream ois1 = null;
		OutputStream NativeOut1 = null;
		ObjectOutputStream oos1 = null;
		InputStream NativeIn2 = null;
		ObjectInputStream ois2 = null;
		OutputStream NativeOut2 = null;
		ObjectOutputStream oos2 = null;
		InputStream NativeIn4 = null;
		ObjectInputStream ois4 = null;
		OutputStream NativeOut4 = null;
		ObjectOutputStream oos4 = null;
		
		InputStream NativeInWifi = null;
		ObjectInputStream oisWifi = null;
		
		/**
		 * 
		 */
	

		try {
			
			clientSocket = new Socket(InetAddress.getLocalHost(),equipementClient.getPort());

			
			while (true) {
				InputStream NativeIn3 = null;
				ObjectInputStream ois3 = null;
				OutputStream NativeOut3 = null;
				NativeIn3 = clientSocket.getInputStream();
				ois3 = new ObjectInputStream(NativeIn3);
				NativeOut3 = clientSocket.getOutputStream();
				new ObjectOutputStream(NativeOut3);
				int choix = (int) ois3.readObject();
				if (choix == 1) {
					/**
					 * 
					 
					///////////////////////////// Envoi des
					///////////////////////////// infos sur le reseau au serveur 
					OutputStream NativeOutWifi = clientSocket.getOutputStream();
					ObjectOutputStream oosWifi = new ObjectOutputStream(NativeOutWifi);
					NativeInWifi = clientSocket.getInputStream();
					oisWifi = new ObjectInputStream(NativeInWifi);
					oosWifi.writeObject(equipementClient.getNetworkInfo());
					oosWifi.flush();*/
					
					///////////////////////////////////// Ajout du serveur dans
					///////////////////////////////////// DA
					
					NativeIn = clientSocket.getInputStream();
					ois = new ObjectInputStream(NativeIn);
					NativeOut = clientSocket.getOutputStream();
					oos = new ObjectOutputStream(NativeOut);

					String res2 = (String) ois.readObject();
					equipementClient.addDa(res2);

					//////////////////////////////////////// Envoi du nom du
					//////////////////////////////////////// client
					//////////////////////////////////////// au
					//////////////////////////////////////// serveur

					oos.writeObject(equipementClient.monNom());
					oos.flush();
				}

				if (choix == 2) {
					NativeOut1 = clientSocket.getOutputStream();
					oos1 = new ObjectOutputStream(NativeOut1);
					NativeIn1 = clientSocket.getInputStream();
					ois1 = new ObjectInputStream(NativeIn1);

					///////////////////////////////////////// Envoi de l
					///////////////////////////////////////// autocertif
					///////////////////////////////////////// du
					///////////////////////////////////////// client au serveur

					equipementClient.envoiCertif(equipementClient.monCertif().x509, oos1);

					///////////////////////////// Client recoit l autocertif du
					///////////////////////////// serveur
					///////////////////////////// ///////////////////////////////////////

					X509Certificate certifRecu1 = equipementClient.certifFactory(ois1);

					certifRecu1.verify(certifRecu1.getPublicKey());

					////////////////////// Envoi du certif signe avec la cle
					////////////////////// privee
					////////////////////// du
					////////////////////// Client pour la clee publique du
					////////////////////// Serveur

					NativeOut2 = clientSocket.getOutputStream();
					oos2 = new ObjectOutputStream(NativeOut2);
					NativeIn2 = clientSocket.getInputStream();
					ois2 = new ObjectInputStream(NativeIn2);

					Certificat certifServeurparClient = new Certificat(equipementClient.monNom(),
							certifRecu1.getSubjectDN().toString().replaceAll("CN=", ""), certifRecu1.getPublicKey(),
							equipementClient.maClePrivee(), 10);

					equipementClient.envoiCertif(certifServeurparClient.x509, oos2);

					/////////////////////////// Reception du certif signe par le
					/////////////////////////// serveur
					/////////////////////////// et dont le sujet est le client

					X509Certificate certifRecu = equipementClient.certifFactory(ois2);
					Boolean verif = certifRecu.getPublicKey().equals(equipementClient.maClePub());
					if (verif) 
					{
						equipementClient.addCa(certifRecu);
					}
					
					
					 
					
					///////////////////////////// Envoi de
					///////////////////////////// DAclient au serveur
					equipementClient.addDa("amiClient");
					NativeOut4 = clientSocket.getOutputStream();
					oos4 = new ObjectOutputStream(NativeOut4);
					NativeIn4 = clientSocket.getInputStream();
					ois4 = new ObjectInputStream(NativeIn4);
					
					oos4.writeObject(equipementClient.equipDa());
					oos4.flush();
					
					//////////////////////////// Reception de DAserver et ajout
					//////////////////////////// de DAserver dans DAclient
					
					equipementClient.putAllDA(ois4, equipementClient);
					
					/////////////////////////// Envoi de CAclient au serveur
					
					Iterator<Entry<String, X509Certificate>> it = equipementClient.certifCa().entrySet().iterator();
					int tailleC = equipementClient.certifCa().size();
					InputStream NativeIn5 = null;
					OutputStream NativeOut5 = null;
					ObjectOutputStream oos5 = null;
					
					NativeOut5 = clientSocket.getOutputStream();
					oos5 = new ObjectOutputStream(NativeOut5);
					NativeIn5 = clientSocket.getInputStream();
					new ObjectInputStream(NativeIn5);
					
					oos5.writeObject(tailleC);
					
					while (it.hasNext()) {
							InputStream NativeIn6 = null;
							OutputStream NativeOut6 = null;
							ObjectOutputStream oos6 = null;
							
							NativeOut6 = clientSocket.getOutputStream();
							oos6 = new ObjectOutputStream(NativeOut6);
							NativeIn6 = clientSocket.getInputStream();
							new ObjectInputStream(NativeIn6);
						
					    	@SuppressWarnings("rawtypes")
							Map.Entry pair = (Map.Entry)it.next();
					    	equipementClient.envoiCertif((X509Certificate) pair.getValue(), oos6);
					       
					    }
					
					//////////////////////// Reception de CAserver
					InputStream NativeIn7 = null;
					ObjectInputStream ois7 = null;
					OutputStream NativeOut7 = null;
					NativeOut7 = clientSocket.getOutputStream();
					new ObjectOutputStream(NativeOut7);
					NativeIn7 = clientSocket.getInputStream();
					ois7 = new ObjectInputStream(NativeIn7);
					
					int tailleS = (int) ois7.readObject();;
					
					while(tailleS != 0) {
						
						InputStream NativeIn8 = null;
						ObjectInputStream ois8 = null;
						OutputStream NativeOut8 = null;
						NativeIn8 = clientSocket.getInputStream();
						ois8 = new ObjectInputStream(NativeIn8);
						NativeOut8 = clientSocket.getOutputStream();
						new ObjectOutputStream(NativeOut8);
						
						X509Certificate certifCa = equipementClient.certifFactory(ois8);
						equipementClient.addCa(certifCa);
						tailleS--;
					}
					
				}
			}
			

		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		clientSocket.close();
	}

	public static void main(String[] args) throws Exception {

		equipementClient = new Equipement("sonyClient", 6003);
		new Client(equipementClient);
		equipementClient.affichage_da();

	}

}
