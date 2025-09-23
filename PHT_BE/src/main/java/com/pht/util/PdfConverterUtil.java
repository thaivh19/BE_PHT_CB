package com.pht.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PdfConverterUtil {

    /**
     * Convert base64 image string thành PDF byte array
     * @param base64Image Base64 encoded image string
     * @return PDF byte array
     * @throws Exception nếu có lỗi trong quá trình convert
     */
    public byte[] convertBase64ImageToPdf(String base64Image) throws Exception {
        if (!StringUtils.hasText(base64Image)) {
            throw new IllegalArgumentException("Base64 image string không được để trống");
        }

        try {
            log.info("Bắt đầu convert base64 image thành PDF, độ dài base64: {}", base64Image.length());

            // Remove data URL prefix nếu có (data:image/png;base64, hoặc data:image/jpeg;base64,)
            String cleanBase64 = base64Image;
            String imageFormat = "png"; // default format
            if (base64Image.contains(",")) {
                String prefix = base64Image.substring(0, base64Image.indexOf(","));
                cleanBase64 = base64Image.substring(base64Image.indexOf(",") + 1);
                
                // Detect image format from data URL
                if (prefix.contains("jpeg") || prefix.contains("jpg")) {
                    imageFormat = "jpeg";
                } else if (prefix.contains("png")) {
                    imageFormat = "png";
                } else if (prefix.contains("gif")) {
                    imageFormat = "gif";
                } else if (prefix.contains("bmp")) {
                    imageFormat = "bmp";
                }
            }

            // Decode base64 thành byte array
            byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);
            log.info("Đã decode base64 thành byte array, kích thước: {} bytes, format: {}", imageBytes.length, imageFormat);

            // Kiểm tra magic bytes để xác định format thực tế
            String actualFormat = detectImageFormat(imageBytes);
            log.info("Detected image format từ magic bytes: {}", actualFormat);

            // Tạo PDF document
            Document document = new Document();
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, pdfOutputStream);

            document.open();

            // Tạo image từ byte array với format cụ thể
            Image image;
            try {
                // Thử tạo image trực tiếp
                image = Image.getInstance(imageBytes);
            } catch (Exception e) {
                log.warn("Không thể tạo image trực tiếp, thử convert qua BufferedImage. Lỗi: {}", e.getMessage());
                // Fallback: convert qua BufferedImage
                image = createImageFromBytes(imageBytes);
            }
            
            // Scale image để fit vào trang PDF
            float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float pageHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            
            if (image.getWidth() > pageWidth || image.getHeight() > pageHeight) {
                image.scaleToFit(pageWidth, pageHeight);
            }

            // Center image trên trang
            float x = (document.getPageSize().getWidth() - image.getScaledWidth()) / 2;
            float y = (document.getPageSize().getHeight() - image.getScaledHeight()) / 2;
            image.setAbsolutePosition(x, y);

            document.add(image);
            document.close();

            byte[] pdfBytes = pdfOutputStream.toByteArray();
            log.info("Convert thành công base64 image thành PDF, kích thước PDF: {} bytes", pdfBytes.length);

            return pdfBytes;

        } catch (IOException e) {
            log.error("Lỗi IO khi convert base64 image thành PDF: ", e);
            throw new Exception("Lỗi IO khi convert image: " + e.getMessage());
        } catch (DocumentException e) {
            log.error("Lỗi Document khi tạo PDF: ", e);
            throw new Exception("Lỗi tạo PDF: " + e.getMessage());
        } catch (Exception e) {
            log.error("Lỗi không xác định khi convert base64 image thành PDF: ", e);
            throw new Exception("Lỗi convert image thành PDF: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra xem string có phải là base64 image hợp lệ không
     * @param base64String String cần kiểm tra
     * @return true nếu là base64 image hợp lệ
     */
    public boolean isValidBase64Image(String base64String) {
        if (!StringUtils.hasText(base64String)) {
            return false;
        }

        try {
            // Remove data URL prefix nếu có
            String cleanBase64 = base64String;
            if (base64String.contains(",")) {
                cleanBase64 = base64String.substring(base64String.indexOf(",") + 1);
            }

            // Kiểm tra format base64
            Base64.getDecoder().decode(cleanBase64);
            
            // Kiểm tra có phải image không (có thể kiểm tra header bytes)
            byte[] decoded = Base64.getDecoder().decode(cleanBase64);
            if (decoded.length < 4) {
                return false;
            }

            // Kiểm tra magic bytes cho các format image phổ biến
            return isImageFormat(decoded);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Detect image format từ magic bytes
     */
    private String detectImageFormat(byte[] imageBytes) {
        if (imageBytes.length < 4) {
            return "unknown";
        }

        // PNG: 89 50 4E 47
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == 0x50 && 
            imageBytes[2] == 0x4E && imageBytes[3] == 0x47) {
            return "png";
        }

        // JPEG: FF D8 FF
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8 && imageBytes[2] == (byte) 0xFF) {
            return "jpeg";
        }

        // GIF: 47 49 46 38
        if (imageBytes[0] == 0x47 && imageBytes[1] == 0x49 && 
            imageBytes[2] == 0x46 && imageBytes[3] == 0x38) {
            return "gif";
        }

        // BMP: 42 4D
        if (imageBytes[0] == 0x42 && imageBytes[1] == 0x4D) {
            return "bmp";
        }

        return "unknown";
    }

    /**
     * Tạo iText Image từ byte array thông qua BufferedImage
     */
    private Image createImageFromBytes(byte[] imageBytes) throws Exception {
        try {
            log.info("Tạo BufferedImage từ byte array, kích thước: {} bytes", imageBytes.length);
            
            // Log một vài byte đầu để debug
            if (imageBytes.length >= 16) {
                StringBuilder hex = new StringBuilder();
                for (int i = 0; i < 16; i++) {
                    hex.append(String.format("%02X ", imageBytes[i]));
                }
                log.info("First 16 bytes (hex): {}", hex.toString());
            }
            
            // Kiểm tra xem có phải là PDF không
            if (isPdfData(imageBytes)) {
                log.warn("Data có vẻ là PDF, không thể convert thành image. Nên sử dụng trực tiếp PDF data.");
                throw new Exception("Data là PDF, không thể convert thành image. Hãy sử dụng trực tiếp PDF data.");
            }
            
            // Tạo BufferedImage từ byte array
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bais);
            
            if (bufferedImage == null) {
                // Thử với các format khác nhau
                log.warn("Không thể đọc image với ImageIO.read(), thử các format khác");
                
                // Thử đọc như JPEG
                bais.reset();
                bufferedImage = ImageIO.read(bais);
                
                if (bufferedImage == null) {
                    // Thử đọc như PNG
                    bais.reset();
                    bufferedImage = ImageIO.read(bais);
                }
                
                if (bufferedImage == null) {
                    throw new Exception("Không thể đọc image từ byte array với bất kỳ format nào");
                }
            }
            
            log.info("Tạo BufferedImage thành công: {}x{} pixels", 
                    bufferedImage.getWidth(), bufferedImage.getHeight());
            
            // Convert BufferedImage thành iText Image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] pngBytes = baos.toByteArray();
            
            log.info("Convert BufferedImage thành PNG bytes: {} bytes", pngBytes.length);
            
            return Image.getInstance(pngBytes);
            
        } catch (Exception e) {
            log.error("Lỗi khi tạo Image từ BufferedImage: ", e);
            throw new Exception("Lỗi convert BufferedImage thành iText Image: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra xem byte array có phải là PDF data không
     */
    private boolean isPdfData(byte[] data) {
        if (data.length < 4) {
            return false;
        }
        
        // PDF magic bytes: %PDF
        return data[0] == 0x25 && data[1] == 0x50 && data[2] == 0x44 && data[3] == 0x46;
    }

    /**
     * Kiểm tra magic bytes để xác định format image
     */
    private boolean isImageFormat(byte[] imageBytes) {
        return !"unknown".equals(detectImageFormat(imageBytes));
    }
}
