package com.pht.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExUtils {

    /**
     * Mask the JSON case sensitive attribute with a passing value.
     *
     * @author Vũ Minh Phúc
     */
    public static String maskingJsonAttribute(String json, String key, String maskChars) {
        return maskingJsonAttribute(json, key, maskChars, false);
    }

    /**
     * Mask the JSON ignore-case attribute with a passing value.
     *
     * @author Vũ Minh Phúc
     */
    public static String maskingJsonAttributeIgnoreCase(String json, String key, String maskChars) {
        return maskingJsonAttribute(json, key, maskChars, true);
    }

    /**
     * Mask the JSON attribute with a passing value.
     *
     * @author Vũ Minh Phúc
     */
    public static String maskingJsonAttribute(String json, String key, String maskChars, boolean ignoreCase) {
        if (json != null && key != null) {
            key = (ignoreCase ? "((?i)\"" : "(\"") + key + "\")";
            String regex = key + "(\\s*:\\s*)(?!(?i)null)((\\[(?![\\[{]).*?]|\".*?\"|\\{(?![\\[{]).*?}|\\w+)|(\\[{2,}.*?]{2,}|\\{{2,}.*?}(?=}$|,)))";
            return json.replaceAll(regex, "$1$2" + (maskChars != null ? maskChars : ""));
        }

        return json;
    }

    /**
     * Check that the image file name is valid.
     */
    public static boolean isValidImageFileName(String fileName) {
        return fileName.matches(".*\\.(?i)(jpg|jpeg|png|gif|bmp)$");
    }
    public static String findMatchRegex(String request, String regex, String subst) {
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(request);
        return matcher.replaceAll(subst);
    }
}
