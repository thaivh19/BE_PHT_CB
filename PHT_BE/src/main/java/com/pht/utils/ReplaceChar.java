package com.pht.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ReplaceChar {
	public static String ReplaceSpecialChar(String values) {
        values = removeChar(values, '^');
        values = removeChar(values, '~');
        values = removeChar(values, '`');
        values = removeChar(values, '\'');
        values = removeChar(values, '?');
        values = removeChar(values, '+');
        values = removeChar(values, '(');
        values = removeChar(values, ')');
        values = removeChar(values, '!');
        values = removeChar(values, '@');
        values = removeChar(values, '#');
        values = removeChar(values, '$');
        values = removeChar(values, '%');
        values = removeChar(values, '&');
        values = removeChar(values, '*');
        values = removeChar(values, '|');
        values = removeChar(values, '<');
        values = removeChar(values, '>');
        return values;
    }

    public static String removeChar(String s, char c) {
        String r = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) {
                r += s.charAt(i);
            }
        }
        return r;
    }

    public String ReplaceCharDB(String values) {
        values = removeChar(values, '\'');
        return values;
    }

    /*
     * public static String replaceUnicode(String values) { Pattern pattern =
     * Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); values =
     * Normalizer.normalize(values, Normalizer.DECOMP, 0); values =
     * pattern.matcher(values).replaceAll("").replace( '\u0111', 'd').replace(
     * '\u0110', 'D'); return values; }
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static String normalize(String values) {
        Class clazz = null;
        int version = 0;
        try {
            Class.forName("java.text.Normalizer$Form");
            clazz = Class.forName("java.text.Normalizer");
            version = 6;

        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName("sun.text.Normalizer");
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            version = 5;
        }
        String result = values;
        if (clazz != null) {
            if (version == 5) {
                try {
                    Class DECOMPClazz = Class
                                    .forName("sun.text.Normalizer$Mode");
                    Object DECOMP = clazz.getField("DECOMP").get(null);
                    Method method = clazz.getMethod("normalize", new Class[] {
                                    String.class, DECOMPClazz, int.class });
                    result = (String) method.invoke(null, new Object[] {
                                    values, DECOMP, 0 });
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                	e.printStackTrace();
                } catch (NoSuchMethodException e) {
                	e.printStackTrace();
                } catch (IllegalAccessException e) {
                	e.printStackTrace();
                } catch (NoSuchFieldException e) {
                	e.printStackTrace();
                } catch (InvocationTargetException e) {
                	e.printStackTrace();
                } catch (ClassNotFoundException e) {
                	e.printStackTrace();
                }
            } else {
                try {
                    Class formClass = Class
                                    .forName("java.text.Normalizer$Form");
                    Object NFD = formClass.getField("NFD").get(null);
                    Method method = clazz.getDeclaredMethod("normalize",
                                    new Class[] { CharSequence.class,
                                                    NFD.getClass() });
                    result = (String) method.invoke(null, new Object[] {
                                    values, NFD });

                } catch (ClassNotFoundException e) {
                	e.printStackTrace();
                } catch (IllegalArgumentException e) {
                	e.printStackTrace();
                } catch (SecurityException e) {
                	e.printStackTrace();
                } catch (IllegalAccessException e) {
                	e.printStackTrace();
                } catch (NoSuchFieldException e) {
                	e.printStackTrace();
                } catch (NoSuchMethodException e) {
                	e.printStackTrace();
                } catch (InvocationTargetException e) {
                	e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String replaceUnicode(String values) {
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        values = normalize(values);
        values = pattern.matcher(values).replaceAll("").replace('\u0111', 'd')
                        .replace('\u0110', 'D')
                        .replace('\u2012', '-')
                        .replace('\u2013', '-')
                        .replace('\u2014', '-')
                        .replace('\u2015', '-')
                        .replace('\u2016', '-');
        return values;
    }
    
    
}
