package com.pht.service;

import com.pht.model.request.BankWebhookRequest;
import com.pht.model.response.BankWebhookResponse;

public interface BankWebhookService {
    
    /**
     * Xử lý webhook từ ngân hàng
     * @param request Dữ liệu từ ngân hàng
     * @return Response trả về cho ngân hàng
     */
    BankWebhookResponse processPaymentNotification(BankWebhookRequest request);

    /**
     * Giả lập dữ liệu thanh toán và gọi xử lý webhook cho danh sách số đơn hàng
     */
    java.util.List<com.pht.model.response.BankWebhookResponse> simulatePaymentForOrders(java.util.List<String> soDonHangList);
}
