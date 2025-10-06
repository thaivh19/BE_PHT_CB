package com.pht.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pht.entity.SBienLai;
import com.pht.model.response.BlThuReportItem;
import com.pht.model.response.KhoBlReportItem;

@Repository
public interface SBienLaiRepository extends BaseRepository<SBienLai, Long> {

    @Query("SELECT s FROM SBienLai s WHERE " +
           "(:mst IS NULL OR LOWER(s.mst) LIKE LOWER(:mst)) AND " +
           "(:tenDvi IS NULL OR LOWER(s.tenDvi) LIKE LOWER(:tenDvi)) AND " +
           "(:maBl IS NULL OR LOWER(s.maBl) LIKE LOWER(:maBl)) AND " +
           "(:soBl IS NULL OR LOWER(s.soBl) LIKE LOWER(:soBl)) AND " +
           "(:soTk IS NULL OR LOWER(s.soTk) LIKE LOWER(:soTk)) AND " +
           "(:maKho IS NULL OR LOWER(s.maKho) LIKE LOWER(:maKho))")
    Page<SBienLai> findBySearchCriteria(@Param("mst") String mst,
                                        @Param("tenDvi") String tenDvi,
                                        @Param("maBl") String maBl,
                                        @Param("soBl") String soBl,
                                        @Param("soTk") String soTk,
                                        @Param("maKho") String maKho,
                                        Pageable pageable);

    @Query("SELECT s FROM SBienLai s WHERE " +
           "(:mst IS NULL OR LOWER(s.mst) LIKE LOWER(:mst)) AND " +
           "(:tenDvi IS NULL OR LOWER(s.tenDvi) LIKE LOWER(:tenDvi)) AND " +
           "(:maBl IS NULL OR LOWER(s.maBl) LIKE LOWER(:maBl)) AND " +
           "(:soBl IS NULL OR LOWER(s.soBl) LIKE LOWER(:soBl)) AND " +
           "(:soTk IS NULL OR LOWER(s.soTk) LIKE LOWER(:soTk)) AND " +
           "(:maKho IS NULL OR LOWER(s.maKho) LIKE LOWER(:maKho))")
    List<SBienLai> findBySearchCriteria(@Param("mst") String mst,
                                        @Param("tenDvi") String tenDvi,
                                        @Param("maBl") String maBl,
                                        @Param("soBl") String soBl,
                                        @Param("soTk") String soTk,
                                        @Param("maKho") String maKho);

    boolean existsByMaBl(String maBl);
    
    boolean existsBySoBl(String soBl);
    
    SBienLai findByMaBl(String maBl);

    /**
     * Báo cáo BL thu: lọc các biên lai của những tờ khai có trạng thái = '04',
     * trong khoảng thời gian theo ngày biên lai, nhóm theo MST và ngày, tính tổng số tiền
     * từ các chi tiết biên lai.
     */
    @Query(value = "SELECT bl.mst, bl.ten_dvi, DATE(bl.ngay_bl) as ngay, SUM(ct.so_tien) as tong_tien, COUNT(DISTINCT bl.id) as so_bien_lai\n"
         + " FROM SBIEN_LAI bl JOIN SBIEN_LAI_CT ct ON ct.bl_id = bl.id\n"
         + " JOIN STO_KHAI tk ON tk.id_bien_lai = bl.id\n"
         + " WHERE tk.trangthai = '04'\n"
         + "   AND bl.ngay_bl >= :fromDate AND bl.ngay_bl < :toDate\n"
         + " GROUP BY bl.mst, bl.ten_dvi, DATE(bl.ngay_bl)", nativeQuery = true)
    List<Object[]> reportBlThuRaw(@Param("fromDate") LocalDateTime fromDate,
                                  @Param("toDate") LocalDateTime toDate);

    /**
     * Tổng hợp theo mã kho (lấy từ StoKhai.maLuuKho) cho các tờ khai trạng thái '04'
     */
    @Query("SELECT tk.maLuuKho AS maKho, SUM(ct.soTien) AS tongTien, COUNT(DISTINCT bl.id) AS soBienLai\n"
         + " FROM SBienLai bl JOIN SBienLaiCt ct ON ct.blId = bl.id\n"
         + " JOIN StoKhai tk ON tk.idBienLai = bl.id\n"
         + " WHERE tk.trangThai = '04'\n"
         + "   AND bl.ngayBl >= :fromDate AND bl.ngayBl < :toDate\n"
         + " GROUP BY tk.maLuuKho")
    List<Object[]> reportByKhoRaw(@Param("fromDate") LocalDateTime fromDate,
                                  @Param("toDate") LocalDateTime toDate);
}
