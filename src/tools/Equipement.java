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

private PaireClesRSA maCle; // La paire de cle de l’equipement.

private AutoCertificat monCert; // Le certificat auto-signe.

private String monNom; // Identite de l’equipement.

private int monPort; // Le numéro de port d’ecoute.

private HashMap<String, X509Certificate> certifCA ;
private HashMap<String, String> equipDA ;

private List<String> addresses ;
private List<String> infoClientWifi;



public Equipement (String nom, int port) throws Exception {

this.monNom = nom;
this.monPort = port;
this.maCle = new PaireClesRSA();
this.monCert = new AutoCertificat(nom, this.maCle, 10);
this.certifCA = new HashMap<String, X509Certificate>();
this.equipDA = new HashMap<String, String>();
this.addresses = new ArrayList<String>();
this.infoClientWifi = new ArrayList<String>();
}


public int getPort() {

	return this.monPort;

	}

public void affichage_ca() {

	System.out.println(certifCA);

	}

public HashMap<String, X509Certificate> certifCa() {
	return certifCA;

}


public List<String> getNetworkInfo() throws SocketException {

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

public void addCa(X509Certificate CACertif) {
	certifCA.put("\nIssuer: " + CACertif.getIssuerDN().getName() + "\nSubject: " + CACertif.getSubjectDN().getName(), CACertif);

}

public boolean verifyNetwork(String infoClient) throws SocketException {
	int count = 0;
	
	String clean = infoClient.substring(1, infoClient.length()-1);
	String[] Allinterfaces = clean.split(", ");
	List<String> interfacesList = new ArrayList<String>();
	for (String interfaces : Allinterfaces) {
		if(!interfacesList.contains(interfaces)) {
			interfacesList.add(interfaces);
		}
	}
	for (String info : interfacesList) {
		
		if (this.getNetworkInfo().contains(info)) {
			count++;
		}
	}
	if (count>=2) return true;
	return false;
	
}

public void addInfoClientWifi(String info) {
	infoClientWifi.add(info);
	
}

public List<String> getInfoClientWifi() {
	return infoClientWifi;
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

public void affichage() {
System.out.println("Nom:"+ "\t" + monNom() );
System.out.println("ClePublique:"+ "\t" + maClePub().toString() );
System.out.println("Certificat de " + monNom() + ":"+ "\t" + afficheMonCertif());
}

public String monNom (){

return monNom;

}

public PublicKey maClePub() {

return maCle.Publique();

}

public PrivateKey maClePrivee() {

return maCle.Privee();

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

	return  monCert;
}

public X509Certificate certifFactory (ObjectInputStream ois) throws CertificateException, ClassNotFoundException, IOException {


	String res = (String) ois.readObject();

	CertificateFactory fact = CertificateFactory.getInstance("X.509");
    InputStream is = new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
    X509Certificate cer = (X509Certificate) fact.generateCertificate(is);

	return cer;

}

public void envoiCertif(X509Certificate certif, ObjectOutputStream oos) throws IOException
{
	StringWriter sw = new StringWriter();
	JcaPEMWriter pw = new JcaPEMWriter(sw);
	pw.writeObject(certif);
	pw.flush();
	pw.close();
	String pemCert = sw.toString();

	oos.writeObject(pemCert);
	oos.flush();
	}

public void putAllCA (ObjectInputStream ois, Equipement equip) throws ClassNotFoundException, IOException{

	@SuppressWarnings("unchecked")
	HashMap<String, X509Certificate> ca = (HashMap<String, X509Certificate>) ois.readObject();
	equip.certifCa().putAll(ca);
}

public void putAllDA (ObjectInputStream ois, Equipement equip) throws ClassNotFoundException, IOException{

	@SuppressWarnings("unchecked")
	HashMap<String, String> da = (HashMap<String, String>) ois.readObject();
	equip.equipDa().putAll(da);
}



}
