package com.pht.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pht.dto.ToKhaiTraCuuResponse;
import com.pht.entity.StoKhai;
import com.pht.entity.StoKhaiCt;
import com.pht.exception.BusinessException;
import com.pht.repository.ToKhaiThongTinChiTietRepository;
import com.pht.repository.ToKhaiThongTinRepository;
import com.pht.service.ToKhaiTraCuuService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ToKhaiTraCuuServiceImpl implements ToKhaiTraCuuService {

    @Autowired
    private ToKhaiThongTinChiTietRepository chiTietRepository;
    
    @Autowired
    private ToKhaiThongTinRepository toKhaiRepository;

    @Override
    public List<ToKhaiTraCuuResponse> traCuuToKhai(String soVanDon, String soHieu) throws BusinessException {
        log.info("Tra cứu tờ khai với SO_VANDON: {}, SO_HIEU: {}", soVanDon, soHieu);
        
        try {
            List<StoKhaiCt> chiTietList = new ArrayList<>();
            
            // Logic tra cứu theo yêu cầu
            if (soVanDon != null && !soVanDon.trim().isEmpty() && 
                soHieu != null && !soHieu.trim().isEmpty()) {
                // Cả hai đều có dữ liệu - tra cứu theo cả hai
                log.info("Tra cứu theo cả SO_VANDON và SO_HIEU");
                chiTietList = chiTietRepository.findBySoVanDonAndSoHieu(soVanDon.trim(), soHieu.trim());
                
            } else if (soVanDon != null && !soVanDon.trim().isEmpty()) {
                // Chỉ có SO_VANDON - tra cứu theo SO_VANDON
                log.info("Tra cứu theo SO_VANDON: {}", soVanDon);
                chiTietList = chiTietRepository.findBySoVanDon(soVanDon.trim());
                
            } else if (soHieu != null && !soHieu.trim().isEmpty()) {
                // Chỉ có SO_HIEU - tra cứu theo SO_HIEU
                log.info("Tra cứu theo SO_HIEU: {}", soHieu);
                chiTietList = chiTietRepository.findBySoHieu(soHieu.trim());
                
            } else {
                // Cả hai đều null hoặc empty
                throw new BusinessException("Vui lòng nhập ít nhất một trong hai thông tin: SO_VANDON hoặc SO_HIEU");
            }
            
            if (chiTietList.isEmpty()) {
                log.info("Không tìm thấy kết quả tra cứu");
                return new ArrayList<>();
            }
            
            log.info("Tìm thấy {} chi tiết tờ khai", chiTietList.size());
            
            // Chuyển đổi sang response và lấy thông tin tờ khai chính
            List<ToKhaiTraCuuResponse> result = chiTietList.stream()
                .map(chiTiet -> {
                    // Lấy thông tin tờ khai chính
                    StoKhai toKhai = toKhaiRepository.findById(chiTiet.getToKhaiThongTinID()).orElse(null);
                    if (toKhai == null) {
                        log.warn("Không tìm thấy tờ khai chính với ID: {}", chiTiet.getToKhaiThongTinID());
                        return null;
                    }
                    return new ToKhaiTraCuuResponse(toKhai, chiTiet);
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
            
            log.info("Trả về {} kết quả tra cứu", result.size());
            return result;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi tra cứu tờ khai: ", e);
            throw new RuntimeException("Lỗi khi tra cứu tờ khai: " + e.getMessage(), e);
        }
    }
}


