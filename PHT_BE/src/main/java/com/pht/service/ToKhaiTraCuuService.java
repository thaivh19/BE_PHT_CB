package com.pht.service;

import java.util.List;

import com.pht.dto.ToKhaiTraCuuResponse;
import com.pht.exception.BusinessException;

public interface ToKhaiTraCuuService {
    
    /**
     * Tra cứu tờ khai theo SO_VANDON và SO_HIEU
     * Nếu một trong hai null thì tra cứu theo thông tin có dữ liệu
     */
    List<ToKhaiTraCuuResponse> traCuuToKhai(String soVanDon, String soHieu) throws BusinessException;
    
}


