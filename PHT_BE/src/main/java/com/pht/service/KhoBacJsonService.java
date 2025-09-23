package com.pht.service;

import java.util.List;

import com.pht.entity.SDoiSoat;
import com.pht.entity.SDoiSoatCt;

public interface KhoBacJsonService {
    
    /**
     * Tạo JSON gửi kho bạc từ dữ liệu đối soát
     */
    String taoJsonGuiKhoBac(SDoiSoat doiSoat, List<SDoiSoatCt> chiTietList);
    
    /**
     * Đọc JSON phản hồi từ kho bạc và xử lý kết quả
     */
    void xuLyJsonPhanHoiTuKhoBac(SDoiSoat doiSoat, String jsonPhanHoi);
    
    /**
     * Lưu chi tiết lệch vào bảng S_DOI_SOAT_CT_LECH
     */
    void luuChiTietLech(Long doiSoatId, Long doiSoatCtId, String soToKhai, 
                       String loaiLech, String moTaLech, 
                       java.math.BigDecimal giaTriHeThong, 
                       java.math.BigDecimal giaTriKhoBac, 
                       String cheDoLech);
    
    /**
     * Cập nhật trạng thái đối soát
     */
    void capNhatTrangThaiDoiSoat(SDoiSoat doiSoat, String trangThai, String msgOutbox);
    
    /**
     * Cập nhật trạng thái đối soát với check_send_kb
     */
    void capNhatTrangThaiDoiSoat(SDoiSoat doiSoat, String trangThai, String msgOutbox, String checkSendKb);
    
    /**
     * Cập nhật trạng thái đối soát với JSON phản hồi
     */
    void capNhatTrangThaiDoiSoat(SDoiSoat doiSoat, String trangThai, String msgOutbox, String checkSendKb, String jsonPhanHoi);
}
