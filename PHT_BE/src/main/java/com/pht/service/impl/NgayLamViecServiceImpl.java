package com.pht.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.entity.NgayLv;
import com.pht.repository.NgayLvRepository;
import com.pht.service.NgayLamViecService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class NgayLamViecServiceImpl implements NgayLamViecService {

    @Autowired
    private NgayLvRepository ngayLvRepository;

    @Override
    public boolean isNgayLamViec(LocalDate ngay) {
        String ngayStr = ngay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return isNgayLamViec(ngayStr);
    }

    @Override
    public boolean isNgayLamViec(String ngayLv) {
        log.debug("Kiểm tra ngày làm việc: {}", ngayLv);
        
        List<NgayLv> ngayLamViecList = ngayLvRepository.findByTrangThai("1");
        
        boolean isNgayLamViec = ngayLamViecList.stream()
                .anyMatch(ngay -> ngay.getNgayLv().equals(ngayLv));
        
        log.debug("Ngày {} là ngày làm việc: {}", ngayLv, isNgayLamViec);
        return isNgayLamViec;
    }

    @Override
    public boolean laNgayLamViec(LocalDate ngay) {
        return isNgayLamViec(ngay);
    }

    @Override
    public LocalDate timNgayLamViecGanNhat() {
        return timNgayLamViecGanNhat(LocalDate.now());
    }

    @Override
    public LocalDate timNgayLamViecGanNhat(LocalDate ngayHienTai) {
        log.info("Tìm ngày làm việc gần nhất trước ngày: {}", ngayHienTai);
        
        List<NgayLv> ngayLamViecList = ngayLvRepository.findByTrangThai("1");
        
        if (ngayLamViecList.isEmpty()) {
            log.warn("Không có ngày làm việc nào trong hệ thống");
            return null;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        Optional<LocalDate> ngayGanNhat = ngayLamViecList.stream()
                .map(ngay -> LocalDate.parse(ngay.getNgayLv(), formatter))
                .filter(ngay -> ngay.isBefore(ngayHienTai))
                .max(LocalDate::compareTo);
        
        if (ngayGanNhat.isPresent()) {
            log.info("Ngày làm việc gần nhất: {}", ngayGanNhat.get());
            return ngayGanNhat.get();
        } else {
            log.warn("Không tìm thấy ngày làm việc nào trước ngày: {}", ngayHienTai);
            return null;
        }
    }

    @Override
    public List<NgayLv> getAllNgayLamViec() {
        log.info("Lấy danh sách tất cả ngày làm việc");
        return ngayLvRepository.findAll();
    }

    @Override
    public List<NgayLv> getNgayLamViecByTrangThai(String trangThai) {
        log.info("Lấy danh sách ngày làm việc theo trạng thái: {}", trangThai);
        return ngayLvRepository.findByTrangThai(trangThai);
    }
    
    @Override
    public String layCotTuNgayLamViec(LocalDate ngay) {
        log.info("Lấy thông tin COT từ ngày làm việc: {}", ngay);
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String ngayStr = ngay.format(formatter);
            
            List<NgayLv> ngayLamViecList = ngayLvRepository.findByTrangThai("1");
            
            Optional<NgayLv> ngayLamViec = ngayLamViecList.stream()
                    .filter(nlv -> nlv.getNgayLv().equals(ngayStr))
                    .findFirst();
            
            if (ngayLamViec.isPresent()) {
                String cot = ngayLamViec.get().getCot();
                log.info("Tìm thấy COT cho ngày {}: {}", ngayStr, cot);
                return cot;
            } else {
                log.warn("Không tìm thấy ngày làm việc cho ngày: {}", ngayStr);
                return "16:00"; // Giá trị mặc định
            }
            
        } catch (Exception e) {
            log.error("Lỗi khi lấy COT từ ngày làm việc: ", e);
            return "16:00"; // Giá trị mặc định
        }
    }
}
