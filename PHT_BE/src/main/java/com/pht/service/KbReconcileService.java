package com.pht.service;

import com.pht.dto.KbReconcileRequest;
import com.pht.exception.BusinessException;

public interface KbReconcileService {
    
    /**
     * Xử lý đối soát từ kho bạc
     * @param request Dữ liệu từ kho bạc
     * @throws BusinessException
     */
    void processKbReconcile(KbReconcileRequest request) throws BusinessException;
}




