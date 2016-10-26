package tools;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class Equipement {

private PaireClesRSA maCle; // La paire de cle de l’equipement.

private AutoCertificat monCert; // Le certificat auto-signe.

private String monNom; // Identite de l’equipement.

private int monPort; // Le numéro de port d’ecoute.

private HashMap<String, X509Certificate> certifCA ; 
private HashMap<String, X509Certificate> certifDA ; 
@SuppressWarnings("rawtypes")
private List addresses ; 



@SuppressWarnings("rawtypes")
public Equipement (String nom, int port) throws Exception {

this.monNom = nom;
this.monPort = port;
this.maCle = new PaireClesRSA();
this.monCert = new AutoCertificat(nom, this.maCle, 10);
this.certifCA = new HashMap<String, X509Certificate>();
this.certifDA = new HashMap<String, X509Certificate>();
this.addresses = new ArrayList();


}



public void affichage_da() {

	System.out.println(certifDA);

}

public int getPort() {

	return this.monPort;

	}

public void affichage_ca() {

	System.out.println(certifCA);

	}


@SuppressWarnings({ "rawtypes", "unchecked"})
public List getNetworkInfo() throws SocketException {
	
	Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	
	
	while (interfaces.hasMoreElements()) {
		
NetworkInterface networkInterface = interfaces.nextElement();
Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

	while (inetAddresses.hasMoreElements()) {
		
	addresses.add(inetAddresses.nextElement());
			}
		}
	return addresses;
		
}

public boolean addCa(X509Certificate CACertif) {
	certifCA.put("certif", CACertif);
	return true; 
}


public boolean addDa(X509Certificate DACertif) {
	certifDA.put("certif", DACertif);
	return true;
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



}