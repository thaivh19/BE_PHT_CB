package com.pht.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonHangKySoRequest {

    private Long idDonHang;
    private String serialNumber;
}



