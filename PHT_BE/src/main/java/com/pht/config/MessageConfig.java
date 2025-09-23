package com.pht.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
//import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.pht.utils.ValidationUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Configuration
@RequiredArgsConstructor
public class MessageConfig {
    
    private final BackEndProperties backendProperties;
    
    @Bean
    @ConditionalOnProperty("backend.message-basenames")
    public MessageSource messageSource() {
        List<String> baseNames = Objects.requireNonNullElse(backendProperties.getMessageBasenames(), new ArrayList<>());
        baseNames.add("classpath:messages");
        
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(backendProperties.getMessageBasenames().toArray(new String[0]));
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(true);
        
        return messageSource;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        resolver.setSupportedLocales(List.of(Locale.US, Locale.ENGLISH, Locale.UK, new Locale("vi")));
        setDefaultLocale(resolver);
        return resolver;
    }

    
    private void setDefaultLocale(AcceptHeaderLocaleResolver resolver) {
        if (!ValidationUtils.isNullOrEmpty(backendProperties.getMessageDefaultLocale())) {
            resolver.setDefaultLocale(new Locale(backendProperties.getMessageDefaultLocale()));
        } else {
            resolver.setDefaultLocale(Locale.ENGLISH);
        }
    }
}
