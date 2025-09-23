package com.pht.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.pht.common.constants.CoreConstants;
import com.pht.common.constants.LogStyle;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "backend")
public class BackEndProperties {
    private boolean migrateDbOnStart;
    private List<String> migrateDbLocations;
    private List<String> messageBasenames;
    private String messageDefaultLocale;
    private boolean localeChangeByParam;
    private String localeHeaderName;
    private Security security;
    private HttpLogging httpLogging;
    private LoggingIntegration loggingIntegration;

    /**
     * Add information like: request_id, start_time, end_time, execution_time to response body.
     */
    private boolean addInfoToResponseBody = true;

    private AsyncProperties async;

    private String redisCommandTopic = "cmd";
    private CoreConstants.RedisSerializer redisSerializer = CoreConstants.RedisSerializer.JSON;

    @Data
    public static class Security {
        /**
         * Set 'false' for authenticated.
         */
        private boolean anyRequestPermitAll = true;
    }

    @Data
    public static class HttpLogging {
        private boolean logRequest;
        private boolean logResponse;
        private LogStyle style = LogStyle.SIMPLE;

        /**
         * Set the max length for the request or response body and truncate if it reaches the max length.
         */
        private int bodyMaxLength;

        private List<String> excludeHeaders;

        /**
         * Hide sensitive information.
         */
        private List<String> maskingAttributes;
    }

    @Data
    public static class LoggingIntegration {
        private boolean sendRequestResponseLog;
        private boolean sendAuditLog;
    }

    @Data
    public static class AsyncProperties {
        private int corePoolSize = 5;
        private int maxPoolSize = 10;
        private int queueCapacity = 30;
        private String threadNamePrefix = "CoreAsyncTaskExecutor-";
        private boolean loggingStatsEnabled = true;
        private int loggingStatsDelayMillis = 30_000;
    }
}
