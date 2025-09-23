package com.pht.job;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pht.entity.SDoiSoat;
import com.pht.service.DoiSoatService;
import com.pht.service.NgayLamViecService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DoiSoatScheduledJob {

    @Autowired
    private DoiSoatService doiSoatService;
    
    @Autowired
    private NgayLamViecService ngayLamViecService;
    
    @Value("${doi-soat.job.cron:0 05 16 * * *}")
    private String cronExpression;

    /**
     * Job đối soát tự động chạy theo lịch trình
     * Mặc định: mỗi ngày lúc 16:30
     */
    @Scheduled(cron = "${doi-soat.job.cron:0 05 16 * * *}")
    public void chayDoiSoatTuDong() {
        log.info("=== BẮT ĐẦU JOB ĐỐI SOÁT TỰ ĐỘNG ===");
        log.info("Cron expression: {}", cronExpression);
        
        try {
            LocalDate ngayHienTai = LocalDate.now();
            log.info("Ngày hiện tại: {}", ngayHienTai);
            
            // Kiểm tra ngày hôm nay có phải là ngày làm việc không
            boolean isNgayLamViec = ngayLamViecService.isNgayLamViec(ngayHienTai);
            log.info("Ngày hôm nay có phải ngày làm việc: {}", isNgayLamViec);
            
            if (!isNgayLamViec) {
                log.info("Hôm nay không phải ngày làm việc, bỏ qua job đối soát");
                return;
            }
            
            // Tìm ngày làm việc gần nhất trước ngày hiện tại
            LocalDate ngayLamViecGanNhat = ngayLamViecService.timNgayLamViecGanNhat(ngayHienTai);
            
            if (ngayLamViecGanNhat == null) {
                log.warn("Không tìm thấy ngày làm việc gần nhất, bỏ qua job đối soát");
                return;
            }
            
            log.info("Ngày làm việc gần nhất: {}", ngayLamViecGanNhat);
            
            // Chạy đối soát từ ngày làm việc gần nhất đến hiện tại
            SDoiSoat doiSoat = doiSoatService.chayDoiSoatTuDongTheoNgayLamViec(ngayLamViecGanNhat, ngayHienTai);
            
            if (doiSoat != null) {
                log.info("Job đối soát hoàn thành thành công với ID: {}", doiSoat.getId());
                log.info("Số tờ khai đối soát: {}, Tổng tiền: {}", doiSoat.getTongSo(), doiSoat.getTongTien());
                
            } else {
                log.info("Job đối soát hoàn thành nhưng không có tờ khai nào để đối soát");
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi chạy job đối soát tự động: ", e);
        }
        
        log.info("=== KẾT THÚC JOB ĐỐI SOÁT TỰ ĐỘNG ===");
    }
    
    
}
