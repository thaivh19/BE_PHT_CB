package com.pht.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import com.pht.common.helper.ApplicationContextProvider;

@Slf4j
public class ClassUtils {

    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();

    static {
        namePrimitiveMap.put("boolean", Boolean.TYPE);
        namePrimitiveMap.put("byte", Byte.TYPE);
        namePrimitiveMap.put("char", Character.TYPE);
        namePrimitiveMap.put("short", Short.TYPE);
        namePrimitiveMap.put("int", Integer.TYPE);
        namePrimitiveMap.put("long", Long.TYPE);
        namePrimitiveMap.put("double", Double.TYPE);
        namePrimitiveMap.put("float", Float.TYPE);
        namePrimitiveMap.put("void", Void.TYPE);
    }

    public static Class<?> getClassByClassName(String className) {
        try {
            return getClass(className);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * It will check the spring application context for beans first.<br/>
     * If there are too many bean instances based on class or no bean in context, then create a new instance by using the no-arg constructor.
     *
     * @param className A class name.
     * @return Object instance.
     */
    public static Object getInstanceByClassName(String className) {
        Class<?> aClass =  getClassByClassName(className);
        return getInstanceByClass(aClass);
    }

    public static Object getInstanceByClass(Class<?> aClass) {
        try {
            if (aClass != null) {
                String[] beanNames = ApplicationContextProvider.getBeanNamesForType(aClass);
                String beanName = beanNames.length > 0 ? beanNames[0] : null;
                if (beanName != null) {
                    Object springBean = ApplicationContextProvider.getBean(beanName, aClass);
                    if (springBean != null) {
                        return springBean;
                    }
                }

                return aClass.getConstructor().newInstance();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static String getRuntimeObjectId(Object object) {
        return object.getClass().getName() + "@" + Integer.toHexString(object.hashCode());
    }

    public static Class<?> getPrimitiveClassByName(String name) {
        return switch (name.toLowerCase()) {
            case "byte" -> Byte.TYPE;
            case "short" -> Short.TYPE;
            case "int" -> Integer.TYPE;
            case "long" -> Long.TYPE;
            case "float" -> Float.TYPE;
            case "double" -> Double.TYPE;
            case "char" -> Character.TYPE;
            case "boolean" -> Boolean.TYPE;
            case "void" -> Void.TYPE;
            default -> throw new IllegalArgumentException("Unsupported primitive type: " + name);
        };
    }

    public static Class<?> getClass(String className) throws ClassNotFoundException {
        Class<?> aClass;
        if (namePrimitiveMap.containsKey(className)) {
            aClass = namePrimitiveMap.get(className);
        } else {
            aClass = Class.forName(className);
        }

        return aClass;
    }

    /**
     * Get full package and class name
     */
    public static String getFullClassName(Object object) {
        return object != null ? object.getClass().getName() : null;
    }
}