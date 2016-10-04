import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Calendar;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;


@SuppressWarnings("deprecation")
public class Certificat {

static private BigInteger seqnum = BigInteger.ZERO;

public X509Certificate x509;

Certificat(String nom, PaireClesRSA cle, int validityDays) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException {

// Constructeur d’un certificat auto-signé avec

// CN = nom, la clé publique contenu dans PaireClesRSA,

// la durée de validité.
	
	// On recupere la cle publique et la cle privee :
	Security.addProvider(new BouncyCastleProvider());
	PublicKey pubkey = cle.Publique();

	PrivateKey privkey = cle.Privee();

	// On cree la structure qui va nous permettre de creer le certificat

	X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();
  
   

	// Le certificat sera valide pour 10 jours

	Calendar expiry = Calendar.getInstance();
	Date startDate =  expiry.getTime();
	expiry.add(Calendar.DAY_OF_YEAR, 10);
	Date expiryDate = expiry.getTime();
	certGen.setNotBefore(startDate);
	certGen.setNotAfter(expiryDate);

	// On le positionne dans le futur certificat

	seqnum=seqnum.add(BigInteger.ONE);
	certGen.setSerialNumber(seqnum);

	// Le nom du proprietaire et du certificateur :
	X500Principal cnName = new X500Principal("CN="+nom);
	certGen.setSubjectDN(cnName); 
	certGen.setIssuerDN(cnName); 

	certGen.setPublicKey(pubkey);
	certGen.setSignatureAlgorithm("sha1WithRSA");

	
	this.x509 = certGen.generate(privkey, "BC");

}

public boolean verifCertif (PublicKey pubkey) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

// Vérification de la signature du certificat à l’aide de la clé publique passée en argument.
	
	x509.verify(pubkey);
	return true;
}

}