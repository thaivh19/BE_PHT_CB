package com.pht.utils;
import java.util.*;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isNullOrEmpty(String st) {
        return st == null || st.isEmpty();
    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    public static boolean isNullOrEmpty(List<?> lst) {
        return lst == null || lst.isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isUUIDString(String input) {
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        return pattern.matcher(input).matches();
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

}
