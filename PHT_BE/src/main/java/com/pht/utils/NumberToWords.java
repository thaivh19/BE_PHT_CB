package com.pht.utils;

import java.util.ArrayList;
import java.util.List;

public class NumberToWords {

	private static final String[] DIGIT_WORDS = new String[] { "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
	  
	  private static final String[] UNIT_WORDS = new String[] { "", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ", "tỷ tỷ"};
	  
	  public static String numberToWords(String numberStr) {
	    if (numberStr == null || numberStr.isEmpty() || !numberStr.matches("\\d+"))
	      throw new IllegalArgumentException("Vui lòng nhập số hợp lệ"); 
	    int length = numberStr.length();
	    if (length > 21)
	      throw new IllegalArgumentException("Số vượt quá 21 chữ số"); 
	    List<String> groups = new ArrayList<>();
	    for (int i = length; i > 0; i -= 3) {
	      int start = Math.max(i - 3, 0);
	      groups.add(0, numberStr.substring(start, i));
	    } 
	    StringBuilder result = new StringBuilder();
	    for (int j = 0; j < groups.size(); j++) {
	      int groupNumber = Integer.parseInt(groups.get(j));
	      if (groupNumber > 0) {
	        String groupWords = groupToWords(groupNumber);
	        result.append(groupWords).append(" ").append(UNIT_WORDS[groups.size() - 1 - j]).append(" ");
	      } 
	    } 
	    return String.valueOf(result.toString().trim().replaceAll("\\s+", " ")) + " đồng";
	  }
	  
	  private static String groupToWords(int number) {
	    StringBuilder result = new StringBuilder();
	    int hundreds = number / 100;
	    int tens = number % 100 / 10;
	    int ones = number % 10;
	    if (hundreds > 0)
	      result.append(DIGIT_WORDS[hundreds]).append(" trăm "); 
	    if (tens > 0) {
	      if (tens == 1) {
	        result.append("mười ");
	      } else {
	        result.append(DIGIT_WORDS[tens]).append(" mươi ");
	      } 
	    } else if (hundreds > 0 && ones > 0) {
	      result.append("lẻ ");
	    } 
	    if (ones > 0)
	      if (ones == 5 && tens > 0) {
	        result.append("lăm");
	      } else {
	        result.append(DIGIT_WORDS[ones]);
	      }  
	    return result.toString().trim();
	  }
	  
	  public static void main(String[] args) {
	    String[] testNumbers = { "0", 
	        "5", 
	        "15", 
	        "123", 
	        "12345", 
	        "123456789", 
	        "1000000000000", 
	        "123456789123456789123" };
	    byte b;
	    int i;
	    String[] arrayOfString1;
	    for (i = (arrayOfString1 = testNumbers).length, b = 0; b < i; ) {
	      String number = arrayOfString1[b];
	      try {
	        System.out.println(String.valueOf(number) + " = " + numberToWords(number));
	      } catch (IllegalArgumentException e) {
	        System.out.println("Lỗi" + e.getMessage());
	      } 
	      b++;
	    } 
	  }
}
