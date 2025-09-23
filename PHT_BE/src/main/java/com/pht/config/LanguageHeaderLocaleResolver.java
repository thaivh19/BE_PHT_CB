package com.pht.config;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;


public class LanguageHeaderLocaleResolver extends AcceptHeaderLocaleResolver {
    
    private String langHeaderName;
    
    public LanguageHeaderLocaleResolver() {
        this.langHeaderName = "Lang";
    }
    
    @Override
    @NonNull
    public Locale resolveLocale(@NonNull HttpServletRequest request) {
        Locale defaultLocale = this.getDefaultLocale();
        String lang = request.getHeader(langHeaderName);
        if (lang == null) {
            return defaultLocale != null ? defaultLocale : request.getLocale();
        }
        
        return Locale.forLanguageTag(lang);
    }
    
    public void setLangHeaderName(String langHeaderName) {
        this.langHeaderName = langHeaderName;
    }
}
