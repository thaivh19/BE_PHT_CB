package com.pht.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class StringUtils {

    public static String defaultIfNull(String input, String defaultValue) {
        return Objects.requireNonNullElse(input, defaultValue);
    }

    public static String replaceAllWhiteChars(String input, String replaceChar) {
        return defaultIfNull(input, "").replaceAll("\\s", defaultIfNull(replaceChar, ""));
    }

    public static boolean isNullOrEmpty(String st) {
        return st == null || st.isEmpty();
    }

    public static String replaceAllWhiteChars(String input) {
        return replaceAllWhiteChars(input, "");
    }

    /**
     * Convert list string to string, separate by delimiter.
     */
    public static String listagg(List<String> elements, String delimiter) {
        return String.join(delimiter, elements);
    }

    public static String toString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static String nvl(Object objInput, String defaultValue) {
        if (objInput == null || "".equals(objInput))
            return defaultValue;
        return objInput.toString();
    }

    public static String trimToEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.trimToEmpty(str);
    }

    public static String formatXml(String unformattedXml) {
        try {
            // Chuyển đổi chuỗi XML thành StreamSource
            StreamSource source = new StreamSource(new StringReader(unformattedXml));

            // Tạo Transformer để format XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Cấu hình để format với indentation
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // Sử dụng StreamResult để lưu kết quả format
            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            // Thực hiện chuyển đổi và format XML
            transformer.transform(source, result);

            // Trả về chuỗi XML đã được format
            return stringWriter.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        String unformattedXml = "<tag><nested>hello</nested></tag>";
//        System.out.println(unformattedXml);
//        String formattedXml = formatXml(unformattedXml);
//        System.out.println(formattedXml);
//    }

}
