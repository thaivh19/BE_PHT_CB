package com.pht.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.ChukySo;

@Repository
public interface ChukySoRepository extends JpaRepository<ChukySo, Long> {
    
    /**
     * Tìm chữ ký số theo serial number
     */
    Optional<ChukySo> findBySerialNumber(String serialNumber);
    
    /**
     * Kiểm tra serial number đã tồn tại chưa
     */
    boolean existsBySerialNumber(String serialNumber);
    
    /**
     * Lấy danh sách chữ ký số đang hoạt động
     */
    @Query("SELECT c FROM ChukySo c WHERE c.isActive = true ORDER BY c.id DESC")
    List<ChukySo> findActiveCertificates();
    
    /**
     * Lấy chữ ký số mặc định
     */
    @Query("SELECT c FROM ChukySo c WHERE c.isDefault = true AND c.isActive = true")
    Optional<ChukySo> findDefaultCertificate();
    
    /**
     * Tìm chữ ký số theo mã doanh nghiệp
     */
    @Query("SELECT c FROM ChukySo c WHERE c.maDoanhNghiep = :maDoanhNghiep AND c.isActive = true")
    List<ChukySo> findByMaDoanhNghiep(@Param("maDoanhNghiep") String maDoanhNghiep);
    
    /**
     * Tìm chữ ký số theo mã số thuế
     */
    @Query("SELECT c FROM ChukySo c WHERE c.maSoThue = :maSoThue AND c.isActive = true")
    List<ChukySo> findByMaSoThue(@Param("maSoThue") String maSoThue);
    
    /**
     * Tìm chữ ký số theo thumbprint
     */
    Optional<ChukySo> findByThumbprint(String thumbprint);
    
    /**
     * Kiểm tra thumbprint đã tồn tại chưa
     */
    boolean existsByThumbprint(String thumbprint);
    
    /**
     * Bỏ đánh dấu mặc định của tất cả chữ ký số
     */
    @Modifying
    @Transactional
    @Query("UPDATE ChukySo c SET c.isDefault = false")
    void clearDefaultCertificates();
    
    /**
     * Bỏ đánh dấu mặc định của tất cả chữ ký số có cùng MST
     */
    @Modifying
    @Transactional
    @Query("UPDATE ChukySo c SET c.isDefault = false WHERE c.maSoThue = :maSoThue")
    void clearDefaultForSameMST(@Param("maSoThue") String maSoThue);
}