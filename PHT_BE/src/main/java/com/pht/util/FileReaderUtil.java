package com.pht.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileReaderUtil {
    
    private static final String HQ_DATA_PATH = "C:/IDA/HQ";
    
    /**
     * Đọc tất cả file trong thư mục C:/IDA/HQ
     */
    public List<String> getAllFilesInDirectory() throws IOException {
        Path directory = Paths.get(HQ_DATA_PATH);
        
        if (!Files.exists(directory)) {
            log.warn("Thư mục {} không tồn tại", HQ_DATA_PATH);
            return List.of();
        }
        
        return Files.list(directory)
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
    
    /**
     * Đọc nội dung file theo tên file
     */
    public String readFileContent(String fileName) throws IOException {
        Path filePath = Paths.get(HQ_DATA_PATH, fileName);
        
        if (!Files.exists(filePath)) {
            log.warn("File {} không tồn tại trong thư mục {}", fileName, HQ_DATA_PATH);
            return null;
        }
        
        return Files.readString(filePath);
    }
    
    /**
     * Đọc nội dung file theo đường dẫn đầy đủ
     */
    public String readFileContentByPath(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            log.warn("File {} không tồn tại", filePath);
            return null;
        }
        
        return Files.readString(path);
    }
    
    /**
     * Tìm file theo pattern trong tên file
     */
    public List<String> findFilesByPattern(String pattern) throws IOException {
        Path directory = Paths.get(HQ_DATA_PATH);
        
        if (!Files.exists(directory)) {
            log.warn("Thư mục {} không tồn tại", HQ_DATA_PATH);
            return List.of();
        }
        
        return Files.list(directory)
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(fileName -> fileName.contains(pattern))
                .collect(Collectors.toList());
    }
    
    /**
     * Kiểm tra thư mục có tồn tại không
     */
    public boolean isDirectoryExists() {
        Path directory = Paths.get(HQ_DATA_PATH);
        boolean exists = Files.exists(directory) && Files.isDirectory(directory);
        log.info("Kiểm tra thư mục {}: {}", HQ_DATA_PATH, exists ? "tồn tại" : "không tồn tại");
        return exists;
    }
    
    /**
     * Tạo thư mục nếu chưa tồn tại
     */
    public boolean createDirectoryIfNotExists() {
        try {
            Path directory = Paths.get(HQ_DATA_PATH);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                log.info("Đã tạo thư mục: {}", HQ_DATA_PATH);
                return true;
            }
            return true;
        } catch (IOException e) {
            log.error("Không thể tạo thư mục {}: {}", HQ_DATA_PATH, e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy đường dẫn thư mục HQ
     */
    public String getHqDataPath() {
        return HQ_DATA_PATH;
    }
    
    /**
     * Lấy danh sách file XML trong thư mục
     */
    public List<String> getXmlFiles() throws IOException {
        return findFilesByPattern(".xml");
    }
    
    /**
     * Lấy danh sách file JSON trong thư mục
     */
    public List<String> getJsonFiles() throws IOException {
        return findFilesByPattern(".json");
    }
    
    /**
     * Tạo file mẫu 320.xml nếu chưa có
     */
    public boolean createSampleXmlFile() {
        try {
            Path filePath = Paths.get(HQ_DATA_PATH, "320.xml");
            if (!Files.exists(filePath)) {
                String sampleXml = "<ThongTinChungTu>" +
                        "<ID_CT>UBNDHP_STC_1000</ID_CT>" +
                        "<So_CT>12345</So_CT>" +
                        "<KyHieu_CT>01/TBNP</KyHieu_CT>" +
                        "<Ngay_CT>2019-12-26</Ngay_CT>" +
                        "<Ma_DV>3700689599</Ma_DV>" +
                        "<Ten_DV>Công ty TNHH Điện Tử FOSTER (Việt Nam)</Ten_DV>" +
                        "<Chuong_NS>000</Chuong_NS>" +
                        "<TieuMuc>2267</TieuMuc>" +
                        "<DiaChi>KCN Việt Nam - Singapore</DiaChi>" +
                        "<Ma_LoaiPhi>PHT01</Ma_LoaiPhi>" +
                        "<Ten_LoaiPhi>Phí sử dụng kết cấu hạ tầng cảng biển</Ten_LoaiPhi>" +
                        "<Ma_DV_ThuPhi>31</Ma_DV_ThuPhi>" +
                        "<Ma_CQT_DV_ThuPhi>STCHP</Ma_CQT_DV_ThuPhi>" +
                        "<Ten_DV_ThuPhi>TP Hải Phòng</Ten_DV_ThuPhi>" +
                        "<So_TK_HQ>10273552333</So_TK_HQ>" +
                        "<Ma_LH>A11</Ma_LH>" +
                        "<Ngay_TK_HQ>2019-12-26</Ngay_TK_HQ>" +
                        "<Ma_HQ>03CC</Ma_HQ>" +
                        "<So_TK_NP>201900282</So_TK_NP>" +
                        "<Ngay_TK_NP>2019-12-26</Ngay_TK_NP>" +
                        "<TKKB>351101071070</TKKB>" +
                        "<Ten_TKKB>Phòng Tàichính – Kế hoạch quận Hải An</Ten_TKKB>" +
                        "<Ma_KB>0063</Ma_KB>" +
                        "<Ten_KB>Kho bạc Nhà nước Hải An</Ten_KB>" +
                        "<SoTien_TO>750000</SoTien_TO>" +
                        "<DienGiai>ID_CT:xxxx;LP:xxx;DVNP:xxxx;DVTP:xxxx;MA_CQT:xxxx;TM:xxxxx;ST:xxxxx;</DienGiai>" +
                        "<ThongTinNopTien>" +
                        "<SoTT>1</SoTT>" +
                        "<Ma_BieuCuoc>TF003_20HK</Ma_BieuCuoc>" +
                        "<Ten_BieuCuoc>Container 20feet hàngkhô </Ten_BieuCuoc>" +
                        "<So_VD>223D</So_VD>" +
                        "<So_Hieu_Container>REWREW43242</So_Hieu_Container>" +
                        "<Don_Gia>250000</Don_Gia>" +
                        "<So_Luong>1</So_Luong>" +
                        "<Don_Vi_Tinh>Đồng/Container</Don_Vi_Tinh>" +
                        "<Thanh_Tien>250000</Thanh_Tien>" +
                        "</ThongTinNopTien>" +
                        "<ThongTinNopTien>" +
                        "<SoTT>2</SoTT>" +
                        "<Ma_BieuCuoc>TF003_20HK</Ma_BieuCuoc>" +
                        "<Ten_BieuCuoc>Container 20feet hàngkhô </Ten_BieuCuoc>" +
                        "<So_VD>223D</So_VD>" +
                        "<So_Hieu_Container>REWREW43242</So_Hieu_Container>" +
                        "<Don_Gia>250000</Don_Gia>" +
                        "<So_Luong>2</So_Luong>" +
                        "<Don_Vi_Tinh>Đồng/Container</Don_Vi_Tinh>" +
                        "<Thanh_Tien>500000</Thanh_Tien>" +
                        "</ThongTinNopTien>" +
                        "</ThongTinChungTu>";
                
                Files.write(filePath, sampleXml.getBytes("UTF-8"));
                log.info("Đã tạo file mẫu: {}", filePath);
                return true;
            }
            return true;
        } catch (IOException e) {
            log.error("Không thể tạo file mẫu 320.xml: {}", e.getMessage());
            return false;
        }
    }
}
