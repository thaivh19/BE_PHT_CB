package com.pht.service;

import java.util.List;

import com.pht.entity.StoKhai;
import com.pht.exception.BusinessException;
import com.pht.model.request.NotificationRequest;
import com.pht.model.request.ToKhaiThongTinRequest;
import com.pht.model.request.UpdateTrangThaiRequest;
import com.pht.model.request.UpdateTrangThaiPhatHanhRequest;
import com.pht.model.response.NotificationResponse;

public interface ToKhaiThongTinService extends BaseService<StoKhai, Long> {
    
    List<StoKhai> getAllToKhaiThongTin();
    
    StoKhai getToKhaiThongTinById(Long id) throws BusinessException;
    
    StoKhai createToKhaiThongTin(ToKhaiThongTinRequest request) throws BusinessException;
    
    StoKhai updateTrangThai(UpdateTrangThaiRequest request) throws BusinessException;
    
    StoKhai updateTrangThaiPhatHanh(UpdateTrangThaiPhatHanhRequest request) throws BusinessException;
    
    NotificationResponse createNotification(NotificationRequest request) throws BusinessException;
    
    List<StoKhai> findByTrangThai(String trangThai);
    
}
