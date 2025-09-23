package com.pht.service;

import java.util.List;
import com.pht.exception.BusinessException;
import com.pht.model.request.ClientCertificateListRequest;
import com.pht.model.response.ChuKySoResponse;

/**
 * Service để xử lý danh sách chữ ký số từ client
 */
public interface ClientCertificateService {
    
    /**
     * Lưu danh sách chữ ký số từ client vào database
     * @param request Danh sách chữ ký số từ client
     * @return Danh sách chữ ký số đã lưu
     * @throws BusinessException Nếu có lỗi trong quá trình lưu
     */
    List<ChuKySoResponse> saveClientCertificates(ClientCertificateListRequest request) throws BusinessException;
    
    /**
     * Lưu một chữ ký số từ client
     * @param certInfo Thông tin chữ ký số từ client
     * @return Chữ ký số đã lưu
     * @throws BusinessException Nếu có lỗi trong quá trình lưu
     */
    ChuKySoResponse saveClientCertificate(ClientCertificateListRequest.ClientCertificateInfo certInfo) throws BusinessException;
}




