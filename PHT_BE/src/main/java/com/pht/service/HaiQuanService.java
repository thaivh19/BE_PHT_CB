package com.pht.service;

import java.util.List;

import com.pht.model.request.LayThongTinHaiQuanRequest;
import com.pht.model.request.ParseHaiQuanDataRequest;
import com.pht.model.response.ThongTinHaiQuanResponse;

public interface HaiQuanService {
    
    List<ThongTinHaiQuanResponse> layThongTinHaiQuan(LayThongTinHaiQuanRequest request);
    
    ThongTinHaiQuanResponse parseHaiQuanResponse(ParseHaiQuanDataRequest request);
    
}
