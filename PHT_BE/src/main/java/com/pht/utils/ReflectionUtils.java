package com.pht.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReflectionUtils {

//    public static <T> List<T> getAllConstantsValueFromClass(Class<?> aClass, Class<T> filterClass) {
//        List<Object> allConstants = getAllConstantsValueFromClass(aClass);
//        return allConstants.stream().filter(filterClass::isInstance).map(filterClass::cast).toList();
//    }

    public static List<Object> getAllConstantsValueFromClass(Class<?> aClass) {
        List<Object> constants = new ArrayList<>();
        if (aClass != null) {
            try {
                Field[] fields = aClass.getDeclaredFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        constants.add(field.get(null));
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        return constants;
    }

    public static Object invokeMethod(String className, String methodName, Object... params) {
        try {
            // Get parameter types
            Class<?>[] paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = params[i].getClass();
            }

            return invokeMethod(className, methodName, paramTypes, params);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static Object invokeMethod(String className, String methodName, Class<?>[] paramTypes, Object... params) {
        try {
            Class<?> cls = ClassUtils.getClassByClassName(className);
            if (cls == null) {
                return null;
            }

            // Get method
            Method method = cls.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);

            // Check method is static
            if (Modifier.isStatic(method.getModifiers())) {
                return method.invoke(null, params);
            } else {
                // Get object instance
                Object instance = ClassUtils.getInstanceByClass(cls);
                if (instance != null) {
                    return method.invoke(instance, params);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return null;
    }

    public static boolean isAnnotationPresent(Object object, Class<? extends Annotation> annotationClass) {
        if (object != null) {
            Class<?> sourceClass = object.getClass();
            return isAnnotationPresent(sourceClass, annotationClass);
        }

        return false;
    }

    public static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        if (element != null) {
            return element.isAnnotationPresent(annotationClass);
        }

        return false;
    }

    public static <A extends Annotation> A getAnnotation(Object object, Class<A> annotationClass) {
        if (object != null) {
            Class<?> sourceClass = object.getClass();
            return getAnnotation(sourceClass, annotationClass);
        }

        return null;
    }

    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationClass) {
        if (element != null) {
            return element.getAnnotation(annotationClass);
        }

        return null;
    }

    public static Field getField(Object object, String name) {
        if (object != null) {
            Class<?> sourceClass = object.getClass();
            return getField(sourceClass, name);
        }

        return null;
    }

    public static Field getField(Class<?> aClass, String name) {
        if (aClass != null && !ValidationUtils.isNullOrEmpty(name)) {
            try {
                return aClass.getDeclaredField(name);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        return null;
    }
}
