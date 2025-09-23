package com.pht.common.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHelper {

    private final MessageSource messageSource;

    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public String getMessage(String key) {
        return getMessage(key, new Object[]{});
    }

    public String getMessage(String key, Object... params) {
        try {
            return messageSource.getMessage(key, params, getLocale());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return key;
        }
    }
}