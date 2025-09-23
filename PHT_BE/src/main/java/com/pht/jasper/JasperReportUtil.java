package com.pht.jasper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
@Service
public class JasperReportUtil {
	private static final Logger logger = LoggerFactory.getLogger(JasperReportUtil.class);
	public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";


    public static File stream2file (InputStream is) throws IOException {
        OutputStream outputStream = null;
        try
        {
        	File tempFile = File.createTempFile(PREFIX, SUFFIX);
            tempFile.deleteOnExit();
            outputStream = new FileOutputStream(tempFile);
            
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return tempFile;
        }
        finally
        {
            if(outputStream != null)
            {
                outputStream.close();
            }
        }
    }


    public static JasperPrint getReport(Connection conn, Map<String, Object> parameters, String pathTemplate, String fileName) throws Exception {
        try (InputStream inputStream = new ClassPathResource("jasper/" + pathTemplate).getInputStream()) {

            // Compile báo cáo từ file JRXML
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            // Điền dữ liệu vào báo cáo
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
            jasperPrint.setName(fileName);

            return jasperPrint;

        } catch (FileNotFoundException e) {
            // Xử lý lỗi khi không tìm thấy file
            throw new RuntimeException("Template file not found in resources/jasper: " + pathTemplate, e);
        } catch (Exception e) {
            // Xử lý các lỗi khác khi tạo báo cáo
            throw new RuntimeException("Error generating report", e);
        }
    }
    
    public static JasperPrint getReportData(JRBeanCollectionDataSource dataSource, Map<String, Object> parameters, String pathTemplate, String fileName) throws Exception {
        try (InputStream inputStream = new ClassPathResource("jasper/" + pathTemplate).getInputStream()) {

            // Compile báo cáo từ file JRXML
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            // Điền dữ liệu vào báo cáo
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            jasperPrint.setName(fileName);

            return jasperPrint;

        } catch (FileNotFoundException e) {
            // Xử lý lỗi khi không tìm thấy file
            throw new RuntimeException("Template file not found in resources/jasper: " + pathTemplate, e);
        } catch (Exception e) {
            // Xử lý các lỗi khác khi tạo báo cáo
            throw new RuntimeException("Error generating report", e);
        }
    }

    public static byte[] doExportData(JRBeanCollectionDataSource dataSource, Map<String, Object> parameters, String pathTemplate, String fileName, String format) throws Exception {
        Exporter exporter = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SimpleOutputStreamExporterOutput seo = null;

        try {
            logger.info("doExport - begin getReport.");
            JasperPrint jasperPrint = getReportData(dataSource, parameters, pathTemplate, fileName);
            logger.info("doExport - end getReport.");
            logger.info("doExport - begin export Report.");

            // Chọn exporter dựa trên format
            if ("PDF".equalsIgnoreCase(format)) {
                exporter = new JRPdfExporter();
                seo = new SimpleOutputStreamExporterOutput(out);
                exporter.setExporterOutput(seo);
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.exportReport();
            } else {
                throw new IllegalArgumentException("Unsupported format: " + format);
            }
//		    OutputStream os = new FileOutputStream(new File("d:/"+fileName));
//		    os.write(out.toByteArray());
//		    os.close();
            byte[] bytes = out.toByteArray();
            logger.info("doExport - end export Report.");
            return bytes;
        } catch (Exception e) {
            logger.error("Error during report export", e);
            throw new RuntimeException("Failed to export report", e);
        } finally {
            // Đảm bảo đóng ByteArrayOutputStream
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("Error closing ByteArrayOutputStream", e);
                }
            }

            // Đảm bảo đóng SimpleOutputStreamExporterOutput
            if (seo != null) {
                try {
                    seo.close();  // This method is used to release resources used by SimpleOutputStreamExporterOutput
                } catch (Exception e) {
                    logger.error("Error closing SimpleOutputStreamExporterOutput", e);
                }
            }
        }
    }
    public static byte[] doExport(Connection conn, Map<String, Object> parameters, String pathTemplate, String fileName, String format) throws Exception {
        Exporter exporter = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SimpleOutputStreamExporterOutput seo = null;

        try {
            logger.info("doExport - begin getReport.");
            JasperPrint jasperPrint = getReport(conn, parameters, pathTemplate, fileName);
            logger.info("doExport - end getReport.");
            logger.info("doExport - begin export Report.");

            // Chọn exporter dựa trên format
            if ("PDF".equalsIgnoreCase(format)) {
                exporter = new JRPdfExporter();
                seo = new SimpleOutputStreamExporterOutput(out);
                exporter.setExporterOutput(seo);
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.exportReport();
            } else {
                throw new IllegalArgumentException("Unsupported format: " + format);
            }
//		    OutputStream os = new FileOutputStream(new File("d:/"+fileName));
//		    os.write(out.toByteArray());
//		    os.close();
            byte[] bytes = out.toByteArray();
            logger.info("doExport - end export Report.");
            return bytes;
        } catch (Exception e) {
            logger.error("Error during report export", e);
            throw new RuntimeException("Failed to export report", e);
        } finally {
            // Đảm bảo đóng ByteArrayOutputStream
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("Error closing ByteArrayOutputStream", e);
                }
            }

            // Đảm bảo đóng SimpleOutputStreamExporterOutput
            if (seo != null) {
                try {
                    seo.close();  // This method is used to release resources used by SimpleOutputStreamExporterOutput
                } catch (Exception e) {
                    logger.error("Error closing SimpleOutputStreamExporterOutput", e);
                }
            }
        }
    }



	

	
}
