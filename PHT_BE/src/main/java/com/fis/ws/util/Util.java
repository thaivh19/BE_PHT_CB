package com.fis.ws.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class Util {
	
		
		
	public static X509Certificate getX509Certificate(String filename)  throws Exception {
	    CertificateFactory fact = CertificateFactory.getInstance("X.509");
	    FileInputStream is = new FileInputStream (filename);
	    X509Certificate cer = (X509Certificate) fact.generateCertificate(is);
	    return cer;
	}
	public static PublicKey getPublicKey(String filename)  throws Exception {
	    X509Certificate cer = getX509Certificate(filename);
	    PublicKey key = cer.getPublicKey();
	    return key;
	}
	public static boolean algEquals(String algURI, String algName) {
	    if (algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))  		  return true;
	    else if (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))    return true;
	    else if (algName.equalsIgnoreCase("EC") &&  algURI.equalsIgnoreCase("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))  return true;
	    else    return false;
	  }
	public static byte[] digest(String dma,byte[] data) throws Exception{
		  MessageDigest md = MessageDigest.getInstance(dma);
		  md.update(data);
		  return md.digest();
    }
    public static  void prettyPrint(Document xml) throws Exception {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			//tf.setOutputProperty(OutputKeys.INDENT, "yes");
			Writer out = new StringWriter();
			tf.transform(new DOMSource(xml), new StreamResult(out));
			System.out.println(out.toString());
	}
    
    
    public static PrivateKey readPrivateKey(KeyFactory kf,String filename) throws Exception {
	    byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    return kf.generatePrivate(spec);
    }
    
    public static PublicKey readPublicKey(KeyFactory kf,String filename)  throws Exception {
	    byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
	    return kf.generatePublic(spec);
    }
    
}
