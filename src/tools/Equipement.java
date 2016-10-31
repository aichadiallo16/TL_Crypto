package tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

public class Equipement {

	private PaireClesRSA maCle; 						// Paire de cle de l'equipement.
	private AutoCertificat monCert; 					// Certificat auto-signe.
	private String monNom; 								// Identite de l'equipement.
	private int monPort; 								// Numero de port d'ecoute.
	private HashMap<String, X509Certificate> certifCA;	// Ensemble des certificats possedes par l'equipement
	private HashMap<String, String> equipDA;			// Ensemble des equipement connus par l'equipement
	private List<String> addresses;						// Ensemble des IP des interfaces presentes sur le reseau

	public Equipement(String nom, int port) throws Exception {

		this.monNom = nom;
		this.monPort = port;
		this.maCle = new PaireClesRSA();
		this.monCert = new AutoCertificat(nom, this.maCle, 10);
		this.certifCA = new HashMap<String, X509Certificate>();
		this.equipDA = new HashMap<String, String>();
		this.addresses = new ArrayList<String>();

	}
	
	public String monNom() {

		return monNom;

	}
	
	public int getPort() {

		return this.monPort;

	}

	public PublicKey maClePub() {
		return maCle.Publique();

	}

	public PrivateKey maClePrivee() {

		return maCle.Privee();

	}

	public void affichage_ca() {

		System.out.println(certifCA);

	}

	public HashMap<String, X509Certificate> certifCa() {
		return certifCA;

	}

	public void addCa(X509Certificate CACertif) {
		certifCA.put(
				"\nIssuer: " + CACertif.getIssuerDN().getName() + "\nSubject: " + CACertif.getSubjectDN().getName(),
				CACertif);
	}

	public void affichage_da() {

		System.out.println(equipDA);

	}

	public HashMap<String, String> equipDa() {
		return equipDA;

	}

	public void addDa(String DAEquip) {
		equipDA.put(DAEquip, DAEquip);

	}

	public HashMap<String, Object> afficheMonCertif() {

		HashMap<String, Object> info = new HashMap<String, Object>();
		info.put("SerialNumber", monCert.x509.getSerialNumber());
		info.put("Issuer", monCert.x509.getIssuerDN());
		info.put("StartDate", monCert.x509.getNotBefore());
		info.put("EndDate", monCert.x509.getNotAfter());
		info.put("SubjectDN", monCert.x509.getSubjectDN());
		info.put("PublicKey", monCert.x509.getPublicKey());

		return info;

	}

	public AutoCertificat monCertif() throws IOException {

		return monCert;
	}

	public X509Certificate certifFactory(ObjectInputStream ois)				// genere un certificat au format x509 a partir du format PEM recu
			throws CertificateException, ClassNotFoundException, IOException {

		String res = (String) ois.readObject();

		CertificateFactory fact = CertificateFactory.getInstance("X.509");
		InputStream is = new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
		X509Certificate cer = (X509Certificate) fact.generateCertificate(is);

		return cer;

	}

	public void envoiCertif(X509Certificate certif, ObjectOutputStream oos) // envoi des certificats au format PEM
			throws IOException { 
		StringWriter sw = new StringWriter();
		JcaPEMWriter pw = new JcaPEMWriter(sw);
		pw.writeObject(certif);
		pw.flush();
		pw.close();
		String pemCert = sw.toString();

		oos.writeObject(pemCert);
		oos.flush();
	}

	public void putAllDA(ObjectInputStream ois, Equipement equip) 	// ajout des equipements recus dans DA
			throws ClassNotFoundException, IOException {

		@SuppressWarnings("unchecked")
		HashMap<String, String> da = (HashMap<String, String>) ois.readObject();
		equip.equipDa().putAll(da);
	}

	public List<String> getNetworkInfo() throws SocketException {	// Obtention de l'ensemble des IP des interfaces appartenant au reseau

		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {

			NetworkInterface networkInterface = interfaces.nextElement();
			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

			while (inetAddresses.hasMoreElements()) {
				addresses.add(inetAddresses.nextElement().toString());
			}
		}
		return addresses;
	}

	public boolean verifyNetwork(String infoClient) 	// Verifier que les interfaces sur le reseau du client sont les memes que celles sur le reseau du serveur
			throws SocketException { 
		int count = 0;

		String clean = infoClient.substring(1, infoClient.length() - 1);
		String[] Allinterfaces = clean.split(", ");
		List<String> interfacesList = new ArrayList<String>();
		for (String interfaces : Allinterfaces) {
			if (!interfacesList.contains(interfaces)) {
				interfacesList.add(interfaces);
			}
		}
		for (String info : interfacesList) {

			if (this.getNetworkInfo().contains(info)) {
				count++;
			}
		}
		if (count >= 2)		// Les listes d'adresses contiennent toujours /127.0.0.1 donc il faut une deuxieme adresse en commun
			return true;
		return false;

	}

}
