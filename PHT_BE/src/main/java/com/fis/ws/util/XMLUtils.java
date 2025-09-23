/**
 * 
 */
package com.fis.ws.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * 
 *
 */
public class XMLUtils {
	private static final Logger log = LoggerFactory.getLogger(XMLUtils.class);
	public static String document2String(Document doc, boolean prettyPrint) throws Exception {
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		
		if (prettyPrint) {
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		}
		
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		
		return writer.toString();
	}
	
	public static <T> String jaxb2String(T o) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(o.getClass());

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        StringWriter writer = new StringWriter();
        marshaller.marshal(o, writer);
        
        return writer.toString();
	}
	public static <T> Object jaxbString2Object(String msg, T o) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(
        		new InputSource(new ByteArrayInputStream(msg.getBytes("utf-8"))));
        JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
		
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return jaxbUnmarshaller.unmarshal(doc);
	}
	public static void saveXMLFile(Document doc, String xmlFolder, String fileName){
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource input = new DOMSource(doc);
			
			File file = new File(xmlFolder);
			if (!file.exists())
				file.mkdir();
			StringBuffer strBuff = new StringBuffer(xmlFolder);
			strBuff.append(fileName).append("-").append(System.currentTimeMillis())
					.append(".xml");
			
			StreamResult output = new StreamResult(new File(strBuff.toString()));
			transformer.transform(input, output);
			log.info("save xml success {}", strBuff.toString());
		} catch (Exception e) {
			log.error("save xml file error", e);
		}
	}
}
