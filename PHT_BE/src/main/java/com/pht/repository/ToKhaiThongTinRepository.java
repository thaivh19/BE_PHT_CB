package com.pht.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pht.entity.StoKhai;

public interface ToKhaiThongTinRepository extends BaseRepository<StoKhai, Long> {
    
    /**
     * Kiểm tra xem số thông báo đã tồn tại chưa
     */
    boolean existsBySoThongBao(String soThongBao);
    
    /**
     * Lấy số lượng tờ khai có số thông báo trong ngày hiện tại để tạo sequence
     */
    @Query("SELECT COUNT(t) FROM StoKhai t WHERE t.soThongBao IS NOT NULL AND t.soThongBao LIKE CONCAT(:datePrefix, '%')")
    long countTodayNotifications(@org.springframework.data.repository.query.Param("datePrefix") String datePrefix);
    
    /**
     * Lấy danh sách tờ khai theo trạng thái
     */
    @Query("SELECT t FROM StoKhai t WHERE t.trangThai = :trangThai ORDER BY t.id DESC")
    List<StoKhai> findByTrangThai(@org.springframework.data.repository.query.Param("trangThai") String trangThai);
    
    /**
     * Lấy tờ khai có TTNH = "01" và ngày tạo <= thời điểm đối soát
     */
    @Query("SELECT t FROM StoKhai t WHERE t.trangThaiNganHang = :ttnh AND t.ngayTt <= :thoiDiemDoiSoat ORDER BY t.id")
    List<StoKhai> findByTrangThaiPhatHanhAndNgayTtLessThanEqual(@Param("ttnh") String ttnh, @Param("thoiDiemDoiSoat") LocalDateTime thoiDiemDoiSoat);
    
    /**
     * Lấy tờ khai có TTNH = "01" và ngày tạo trong khoảng thời gian đối soát
     */
    @Query("SELECT t FROM StoKhai t WHERE t.trangThaiNganHang = :ttnh AND t.ngayTt >= :thoiDiemBatDau AND t.ngayTt <= :thoiDiemKetThuc ORDER BY t.id")
    List<StoKhai> findByTrangThaiNganHangAndNgayTtBetween(@Param("ttnh") String ttnh, @Param("thoiDiemBatDau") LocalDateTime thoiDiemBatDau, @Param("thoiDiemKetThuc") LocalDateTime thoiDiemKetThuc);
    
    /**
     * Lấy tờ khai có TTNH = "02" hoặc "03" và ngày tạo trong khoảng thời gian đối soát
     */
    @Query("SELECT t FROM StoKhai t WHERE t.trangThaiNganHang IN ('02', '03') AND t.ngayTt >= :thoiDiemBatDau AND t.ngayTt <= :thoiDiemKetThuc ORDER BY t.id")
    List<StoKhai> findByTrangThaiNganHangInAndNgayTtBetween(@Param("thoiDiemBatDau") LocalDateTime thoiDiemBatDau, @Param("thoiDiemKetThuc") LocalDateTime thoiDiemKetThuc);
    
    /**
     * Tìm tờ khai theo mã doanh nghiệp khai phí và số tờ khai
     */
    List<StoKhai> findByMaDoanhNghiepKhaiPhiAndSoToKhai(String maDoanhNghiepKhaiPhi, String soToKhai);

    /**
     * Lấy danh sách tờ khai theo trạng thái ngân hàng (TT_NH)
     */
    List<StoKhai> findByTrangThaiNganHang(String trangThaiNganHang);
    
    /**
     * Dành cho AutoTask: lọc theo TT_NH, idBienLai null và (tùy chọn) tổng phí < ngưỡng
     */
    @Query("SELECT t FROM StoKhai t WHERE t.trangThaiNganHang = :ttnh AND t.idBienLai IS NULL AND (:maxFee IS NULL OR t.tongTienPhi < :maxFee)")
    List<StoKhai> findAuto(@Param("ttnh") String ttnh, @Param("maxFee") BigDecimal maxFee);
    
}
