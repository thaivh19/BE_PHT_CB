package com.pht.common.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.stereotype.Component;

import com.pht.common.constants.CoreConstants;
import com.pht.utils.HttpUtils;

import java.lang.reflect.Method;
import java.net.InetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoreHelper {

    private final Environment env;

    public String getApplicationName() {
        String appName = env.getProperty("spring.application.name", "");
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostName = inetAddress.getHostName();
            String hostAddress = inetAddress.getHostAddress();
            appName += ":"
                    + (hostName != null ? hostName : "unknown_host_name")
                    + ":"
                    + (hostAddress != null ? hostAddress : "unknown_host_address");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return appName;
    }

    public static String getCurrentRequestId() {
        Object requestId = HttpUtils.getRequestAttribute(CoreConstants.RequestAttribute.REQUEST_ID);
        return requestId != null ? requestId.toString() : null;
    }
}