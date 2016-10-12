package tools;
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

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.x509.X509V1CertificateGenerator;


@SuppressWarnings("deprecation")
public class AutoCertificat {

static private BigInteger seqnum = BigInteger.ZERO;

public X509Certificate x509;

AutoCertificat(String nom, PaireClesRSA cle, int validityDays) throws InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, CertificateException, OperatorCreationException {

	// Le nom du proprietaire et du certificateur :
	
	 
		X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
		nameBuilder.addRDN(BCStyle.CN, nom);
		
		X500Name cnName = nameBuilder.build();
		
		// Le certificat sera valide pour 10 jours

		Calendar expiry = Calendar.getInstance();
		Date startDate =  expiry.getTime();
		expiry.add(Calendar.DAY_OF_YEAR, 10);
		Date expiryDate = expiry.getTime();

	seqnum=seqnum.add(BigInteger.ONE);


	// On le positionne dans le futur certificat

		seqnum=seqnum.add(BigInteger.ONE);
		ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1WithRSA").build(cle.Privee());
		

		JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(cnName, seqnum, startDate, expiryDate, cnName, cle.Publique()) ;
		X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(builder.build(contentSigner));
		certificate.verify(certificate.getPublicKey());
		this.x509 = certificate;

	}



public boolean verifCertif (PublicKey pubkey) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

// Vérification de la signature du certificat à l’aide de la clé publique passée en argument.
	
	x509.verify(pubkey);
	return true;
}

}