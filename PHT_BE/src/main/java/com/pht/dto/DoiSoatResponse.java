package com.pht.dto;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pht.entity.SDoiSoat;

import lombok.Data;

@Data
public class DoiSoatResponse {
    
    private Long id;
    private String pkgId;
    private Integer lanDs;
    private String soBk;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String ngayBk;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private String ngayDs;
    
    private Integer tongSo;
    
    @JsonProperty("tongTien")
    private String tongTienFormatted; // Format không có .00
    
    private String trangThai;
    private String nhDs;
    private String kbDs;
    private Integer tongSoTkDds;
    
    private List<DoiSoatCtResponse> chiTietList;
    
    public DoiSoatResponse(SDoiSoat doiSoat) {
        this.id = doiSoat.getId();
        this.pkgId = doiSoat.getPkgId();
        this.lanDs = doiSoat.getLanDs();
        this.soBk = doiSoat.getSoBk();
        
        // Format ngày theo dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.ngayBk = doiSoat.getNgayBk() != null ? doiSoat.getNgayBk().format(formatter) : null;
        this.ngayDs = doiSoat.getNgayDs() != null ? doiSoat.getNgayDs().format(formatter) : null;
        
        this.tongSo = doiSoat.getTongSo();
        
        // Format tổng tiền theo định dạng xxx.xxx.xxx.xxx
        if (doiSoat.getTongTien() != null) {
            BigDecimal tongTien = doiSoat.getTongTien();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator('.');
            DecimalFormat decimalFormatter = new DecimalFormat("#,###", symbols);
            this.tongTienFormatted = decimalFormatter.format(tongTien);
        } else {
            this.tongTienFormatted = null;
        }
        
        this.trangThai = doiSoat.getTrangThai();
        this.nhDs = doiSoat.getNhDs();
        this.kbDs = doiSoat.getKbDs();
        // this.tongSoTkDds = doiSoat.getTongSoTkDds(); // Tạm thời comment vì getter chưa được generate
        
        // Convert chi tiết list sang DoiSoatCtResponse để format tiền
        if (doiSoat.getChiTietList() != null) {
            this.chiTietList = doiSoat.getChiTietList().stream()
                    .map(DoiSoatCtResponse::new)
                    .collect(java.util.stream.Collectors.toList());
        } else {
            this.chiTietList = null;
        }
    }
}
