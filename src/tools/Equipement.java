package tools;
import java.io.IOException;
import java.io.StringWriter;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;

public class Equipement {

private PaireClesRSA maCle; // La paire de cle de l’equipement.

private AutoCertificat monCert; // Le certificat auto-signe.

private String monNom; // Identite de l’equipement.

private int monPort; // Le numéro de port d’ecoute.
@SuppressWarnings("rawtypes")
private HashMap certifCA ; 


@SuppressWarnings("rawtypes")
public Equipement (String nom, int port) throws Exception {

this.monNom = nom;
this.monPort = port;
this.maCle = new PaireClesRSA();
this.monCert = new AutoCertificat(nom, this.maCle, 10);
this.certifCA = new HashMap();
}

public void affichage_da() {

// Affichage de la liste des équipements de DA.

}

public int getPort() {

	return this.monPort;

	}

public void affichage_ca() {

	System.out.println(certifCA);

	}

@SuppressWarnings({ "rawtypes", "unchecked" })
public HashMap addCa(X509Certificate CACertif) {

	
	certifCA.put("certif", CACertif);
	return certifCA;


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

@SuppressWarnings({ "unchecked", "rawtypes" })
public HashMap afficheMonCertif() {

HashMap info = new HashMap(); 
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