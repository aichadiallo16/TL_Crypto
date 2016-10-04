import java.security.PublicKey;
import java.util.HashMap;

public class Equipement {

private PaireClesRSA maCle; // La paire de cle de l’equipement.

private Certificat monCert; // Le certificat auto-signe.

private String monNom; // Identite de l’equipement.

private int monPort; // Le numéro de port d’ecoute.



Equipement (String nom, int port) throws Exception {

this.monNom = nom;
this.monPort = port;
this.maCle = new PaireClesRSA();
this.monCert = new Certificat(nom, this.maCle, 10);

}

public void affichage_da() {

// Affichage de la liste des équipements de DA.

}

public void affichage_ca() {

// Affichage de la liste des équipements de CA.

}

public void affichage() {
System.out.println("Nom:"+ "\t" + monNom() );
System.out.println("ClePublique:"+ "\t" + maClePub().toString() );
System.out.println("Certificat de " + monNom() + ":"+ "\t" + monCertif());
}

public String monNom (){

return monNom;

}

public PublicKey maClePub() {

return maCle.Publique();

}

public HashMap monCertif() {
HashMap info = new HashMap(); 
info.put("SerialNumber", monCert.x509.getSerialNumber());
info.put("Issuer", monCert.x509.getIssuerDN());
info.put("StartDate", monCert.x509.getNotBefore());
info.put("EndDate", monCert.x509.getNotAfter());
info.put("SubjectDN", monCert.x509.getSubjectDN());
info.put("PublicKey", monCert.x509.getPublicKey());

return info;

}

}