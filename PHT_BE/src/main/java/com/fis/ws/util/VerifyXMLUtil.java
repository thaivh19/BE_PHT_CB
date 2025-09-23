package com.fis.ws.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.utils.Base64;
import org.apache.xml.security.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class VerifyXMLUtil {
    private static final Logger log = LoggerFactory.getLogger(VerifyXMLUtil.class);
    
    public static boolean verifyXML(Document doc, String cerPath) throws Exception {
		org.apache.xml.security.Init.init();
		boolean b;
//		System.setProperty("com.ibm.crypto.provider.DoRSATypeChecking", "false");				
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		xpath.setNamespaceContext(new DSNamespaceContext());

		Element sigElement =(Element) xpath.evaluate("//ds:Signature[1]", doc, XPathConstants.NODE);
		XMLSignature xmlSignature = new XMLSignature(sigElement,"");
		KeyInfo ki = xmlSignature.getKeyInfo();
		SignedInfo si=xmlSignature.getSignedInfo();		
		String dma = (String) xpath.evaluate("//ds:DigestMethod[1]/@Algorithm", sigElement, XPathConstants.STRING);
		dma = dma.replaceAll(Constants.SignatureSpecNS, "");		
		Canonicalizer c14n = Canonicalizer.getInstance(si.getCanonicalizationMethodURI());		
 	    //get siDigest
		Element siElement=(Element) xpath.evaluate("//ds:SignedInfo[1]", sigElement, XPathConstants.NODE);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		c14n.canonicalizeSubtree(siElement, baos);
		byte[]  siC14 = baos.toByteArray();
		byte[]  siDigest=Util.digest(dma,siC14);
		log.info("SignedInfo Digest: {}", Arrays.toString(siDigest));		
		//decrypt signatureValue
		byte[] signatureValue= xmlSignature.getSignatureValue();
		Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, ki.getPublicKey());
	    byte[] decryptDigest=cipher.doFinal(signatureValue);
	    decryptDigest = Arrays.copyOfRange  (decryptDigest, decryptDigest.length - 20, decryptDigest.length);
	    log.info("Signature value decrypt Digest: {}", Arrays.toString(decryptDigest));		
		b=Arrays.equals(decryptDigest,siDigest);
		log.info(String.valueOf(b));	
		if(!b) return false;
		//get digest
		String  digestStr=(String) xpath.evaluate("//ds:DigestValue[1]", sigElement, XPathConstants.STRING);
		byte[]  digestValue=Base64.decode(digestStr);
		log.info("DigestValue decode: {}", Arrays.toString(digestValue));		
		//xoa sigElement 
		sigElement.getParentNode().removeChild(sigElement);
	 	doc.normalize();
 	    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		c14n.canonicalizeSubtree(siElement, baos2);
		byte[]  dataValue = baos2.toByteArray();
 	    
 	    //tinh lai digest
 	    byte[] dataDigestValue=Util.digest(dma,dataValue);
 	    log.info("Data Digest: {}", Arrays.toString(dataDigestValue)); 	   
 	    b=Arrays.equals(digestValue,dataDigestValue);
 	    log.info(String.valueOf(b)); 
 	    if(!b) return false;
 	    //check public key 	   
 	    FileInputStream in = null;
	 	try {
			in = new FileInputStream(cerPath);
		} catch (IOException e) {
			throw new Exception("Path of certificate file is not correct");
		}	
	 	CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cer = (X509Certificate) cf.generateCertificate(in);	
		log.info("XML signature with serial {}", ki.getX509Certificate().getSerialNumber());
		log.info("Customs serial {}", cer.getSerialNumber());
		if(cer.getSerialNumber().equals(ki.getX509Certificate().getSerialNumber())){
			log.info("Verify Custom Success");
		}else{
			log.info("Verify Custom Failed : Certificate of custom invalid");
			return false;
		}	
 	    return b;
	}
	/*verify xml standard java*/
	public static boolean validateSignature(Document doc, String cerPath){
		log.info("Begin validate signature with cert file {}",cerPath);
		// Disable secure validation
		boolean ckValidate = false;
		try {
			CertificateFactory cf;
			cf = CertificateFactory.getInstance("X.509");
			FileInputStream in = new FileInputStream(cerPath);
			X509Certificate cer = (X509Certificate) cf.generateCertificate(in);
			log.info("Serial in cert file {}",cer.getSerialNumber().toString(16));
			NodeList nl = doc.getElementsByTagNameNS(javax.xml.crypto.dsig.XMLSignature.XMLNS, "Signature");
	        if (nl.getLength() == 0) {
	            throw new Exception("Cannot find Signature element");
	        }
	        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
	        DOMValidateContext valContext = new DOMValidateContext(KeySelector.singletonKeySelector(cer.getPublicKey()), nl.item(nl.getLength()-1));
	        javax.xml.crypto.dsig.XMLSignature signature = fac.unmarshalXMLSignature(valContext);
	        ckValidate = signature.validate(valContext);
	        log.info("End validate signature {}",ckValidate);
		} catch (Exception e) {
			log.error("validate signature error", e);
		}
        return ckValidate;
	}
}
