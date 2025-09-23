package com.pht.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SequenceGenerator {
    
    private static final AtomicLong sequenceCounter = new AtomicLong(1);
    private static String currentYear = "";
    
    /**
     * Generate số tiếp nhận khai phí theo format [YYYY][sequence tăng dần]
     * Tổng cộng 12 số: 4 số năm + 8 số sequence
     */
    public String generateSoTiepNhanKhaiPhi() {
        String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        
        // Reset sequence nếu năm mới
        if (!year.equals(currentYear)) {
            currentYear = year;
            sequenceCounter.set(1);
        }
        
        // Lấy sequence hiện tại và tăng lên
        long sequence = sequenceCounter.getAndIncrement();
        
        // Format: YYYY + 8 số sequence (padding với 0)
        return year + String.format("%08d", sequence);
    }
    
    /**
     * Reset sequence counter (dùng cho testing)
     */
    public void resetSequence() {
        sequenceCounter.set(1);
        currentYear = "";
    }
}
