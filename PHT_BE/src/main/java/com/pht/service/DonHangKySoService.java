package com.pht.service;

import com.pht.exception.BusinessException;

public interface DonHangKySoService {
    
    String kySoDonHang(Long idDonHang, String serialNumber) throws BusinessException;
}



