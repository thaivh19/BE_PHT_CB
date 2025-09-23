package com.pht.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pht.entity.SDoiSoatCt;

import lombok.Data;

@Data
public class DoiSoatCtResponse {
    
    private Long id;
    private Long doiSoatId;
    private Long stoKhaiId;
    private String soToKhai;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String ngayToKhai;
    
    private String soTnKp;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String ngayTnKp;
    
    private String maDoanhNghiep;
    private String tenDoanhNghiep;
    
    @JsonProperty("tongTienPhi")
    private String tongTienPhiFormatted; // Format theo xxx.xxx.xxx.xxx
    
    private String transId;
    private String nganHang;
    private String nhDs;
    private String kbDs;
    private String ghiChu;
    
    public DoiSoatCtResponse(SDoiSoatCt doiSoatCt) {
        this.id = doiSoatCt.getId();
        this.doiSoatId = doiSoatCt.getDoiSoatId();
        this.stoKhaiId = doiSoatCt.getStoKhaiId();
        this.soToKhai = doiSoatCt.getSoToKhai();
        
        // Format ngày theo dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.ngayToKhai = doiSoatCt.getNgayToKhai() != null ? doiSoatCt.getNgayToKhai().format(formatter) : null;
        this.ngayTnKp = doiSoatCt.getNgayTnKp() != null ? doiSoatCt.getNgayTnKp().format(formatter) : null;
        
        this.soTnKp = doiSoatCt.getSoTnKp();
        this.maDoanhNghiep = doiSoatCt.getMaDoanhNghiep();
        this.tenDoanhNghiep = doiSoatCt.getTenDoanhNghiep();
        
        // Format tổng tiền phí theo định dạng xxx.xxx.xxx.xxx
        if (doiSoatCt.getTongTienPhi() != null) {
            BigDecimal tongTienPhi = doiSoatCt.getTongTienPhi();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator('.');
            DecimalFormat decimalFormatter = new DecimalFormat("#,###", symbols);
            this.tongTienPhiFormatted = decimalFormatter.format(tongTienPhi);
        } else {
            this.tongTienPhiFormatted = null;
        }
        
        this.transId = doiSoatCt.getTransId();
        this.nganHang = doiSoatCt.getNganHang();
        this.nhDs = doiSoatCt.getNhDs();
        this.kbDs = doiSoatCt.getKbDs();
        this.ghiChu = doiSoatCt.getGhiChu();
    }
}
