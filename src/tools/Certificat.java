package tools;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;





public class Certificat {

static private BigInteger seqnum = BigInteger.ZERO;
public X509Certificate x509;


Certificat(String Issuer, String Subject, PaireClesRSA clePub, PaireClesRSA clePriv, int validityDays) throws InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, CertificateException, OperatorCreationException {
	Security.addProvider(new BouncyCastleProvider());
	
	
	// Le nom du proprietaire et du certificateur :
		
 
	X500NameBuilder nameBuilderIs = new X500NameBuilder(BCStyle.INSTANCE);
	X500NameBuilder nameBuilderSu = new X500NameBuilder(BCStyle.INSTANCE);
	nameBuilderIs.addRDN(BCStyle.CN, Issuer);
	nameBuilderSu.addRDN(BCStyle.CN, Subject);
	X500Name subject = nameBuilderSu.build();
	X500Name issuer = nameBuilderIs.build();
	
	// Le certificat sera valide pour 10 jours

	Calendar expiry = Calendar.getInstance();
	Date startDate =  expiry.getTime();
	expiry.add(Calendar.DAY_OF_YEAR, 10);
	Date expiryDate = expiry.getTime();
	

	// On le positionne dans le futur certificat

	seqnum=seqnum.add(BigInteger.ONE);
	ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1WithRSA").build(clePub.Privee());
	

	JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, seqnum, startDate, expiryDate, subject, clePub.Publique()) ;
	X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(builder.build(contentSigner));
	certificate.verify(certificate.getPublicKey());
	this.x509 = certificate;

}

public HashMap<String, Object> monCertif() {
HashMap<String, Object> info = new HashMap<String, Object>(); 
info.put("SerialNumber", x509.getSerialNumber());
info.put("Issuer", x509.getIssuerDN());
info.put("StartDate", x509.getNotBefore());
info.put("EndDate", x509.getNotAfter());
info.put("SubjectDN", x509.getSubjectDN());
info.put("PublicKey", x509.getPublicKey());

return info;

}
public void verifCertif (PublicKey pubkey) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {

// Vérification de la signature du certificat à l’aide de la clé publique passée en argument.
	
	this.x509.verify(pubkey);
	//return (x509.getPublicKey().equals(pubkey));

}

}