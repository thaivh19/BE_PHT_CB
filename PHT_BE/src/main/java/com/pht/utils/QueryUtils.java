package com.pht.utils;

import jakarta.persistence.Tuple;

import java.util.List;

import com.pht.common.PageInfo;

public class QueryUtils {

    public static int getFirstResult(int pageNumber, int pageSize) {
        return (pageNumber < 0) ? 0 : (pageNumber * pageSize);
    }

    public static int getPageNumber(PageInfo pageInfo) {
        return pageInfo != null ? pageInfo.getPageNumber() : 1;
    }

    public static int getNumberOfElements(List<?> data) {
        return data != null ? data.size() : 0;
    }

    public static int getPageSize(PageInfo pageInfo, List<?> data) {
        return pageInfo != null ? pageInfo.getPageSize() : data != null ? data.size() : 0;
    }

    public static int getTotalPage(long total, int size) {
        return size == 0 ? 1 : (int) Math.ceil((double) total / (double) size);
    }

    public static String createLikeValue(String value) {
        if (!ValidationUtils.isNullOrEmpty(value)) {
            return "%" + value.trim() + "%";
        }

        return "";
    }

    public static <T> T getValueFromTuple(Tuple tuple, String key, Class<T> clazz) {
        try {
            return (T) tuple.get(key);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Get paging offset
     */
    public static int getOffset(int index, int size) {
        return index * size;
    }

    public static String escapeSqlChar(String input) {
        if (!ValidationUtils.isNullOrEmpty(input)) {
            input = input.replaceAll("/", "//");
            input = input.replaceAll("_", "/_");
            input = input.replaceAll("%", "/%");
        }

        return input;
    }
}
