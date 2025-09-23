package com.pht.common.helper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    static LoadingCache<String, Object> cachedBean;

    static {
        cachedBean = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(1))
                .build(key -> {
                    log.info("Get bean '{}' from context", key);
                    Object bean = null;
                    try {
                        bean = applicationContext.getBean(key);
                        log.info("Got and cached bean '{}'", key);
                    } catch (Exception ex) {
                        log.debug(ex.getMessage(), ex);
                    }
                    return bean;
                });
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> aClass) {
        try {
            return applicationContext.getBean(aClass);
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> T getBean(String beanName, Class<T> aClass) {
        try {
            return applicationContext.getBean(beanName, aClass);
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> Optional<T> getCachedBean(String beanName, Class<T> aClass) {
        try {
            Object bean = cachedBean.get(beanName);
            if (bean != null && aClass.isAssignableFrom(bean.getClass())) {
                return Optional.of(aClass.cast(bean));
            }
            return Optional.empty();
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Object getBean(String beanName) {
        try {
            return applicationContext.getBean(beanName);
        } catch (Exception ex) {
            log.debug(ex.getMessage(), ex);
            return null;
        }
    }

    public static String[] getBeanNamesForType(Class<?> type) {
        try {
            return applicationContext.getBeanNamesForType(type);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return new String[0];
    }

    public static void registerBean(String name, Class<?> aClass) {
        getBeanDefinitionRegistry().registerBeanDefinition(name, new RootBeanDefinition(aClass));
    }

    public static void registerBean(String name, BeanDefinition beanDefinition) {
        getBeanDefinitionRegistry().registerBeanDefinition(name, beanDefinition);
    }

    public static void removeBean(String name) {
        try {
            BeanDefinitionRegistry registry = getBeanDefinitionRegistry();
            if (registry.containsBeanDefinition(name)) {
                registry.removeBeanDefinition(name);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static BeanDefinitionRegistry getBeanDefinitionRegistry() {
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        return (BeanDefinitionRegistry) factory;
    }
}
