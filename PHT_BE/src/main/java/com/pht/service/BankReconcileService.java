package com.pht.service;

import com.pht.dto.BankReconcileRequest;
import com.pht.exception.BusinessException;

public interface BankReconcileService {
    
    /**
     * Xử lý đối soát với ngân hàng và lưu JSON vào SLOG_NH_KB
     */
    void processBankReconcile(BankReconcileRequest request) throws BusinessException;
}




