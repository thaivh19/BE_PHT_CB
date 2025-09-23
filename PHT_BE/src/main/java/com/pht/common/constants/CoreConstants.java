package com.pht.common.constants;

public class CoreConstants {
    public static final String SECRET_KEY = "idontknow";
    public static final String SYSTEM_USER = "system";
    public static final String UUID_GENERATOR = "uuid2";
    public static final String CUSTOM_UUID = "custom-uuid";
    public static final String CUSTOM_UUID_GENERATOR = "com.pht.bss.common.helper.UuidGenerator";
    public static final String UUID_CHAR_TYPE = "uuid-char";
    public static final String SPRING_MODEL = "spring"; // for mapstruct componentModel
    public static final String THREAD_POOL_TASK_EXECUTOR = "threadPoolTaskExecutor";

    public static final String[] WHITELIST_SWAGGER_URI = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    public static final String[] WHITELIST_URI = {
            "/webjars/**",
            "/**/login",
            "/error/**"
    };

    public static final class Header {
        public static final String TENANT_ID = "X-TenantID";
        public static final String LANGUAGE = "Lang";
        public static final String API_TYPE = "Api-Type";
        public static final String FUNCTION_NAME = "Function-Name";
    }

    public static final class RequestAttribute {
        public static final String REQUEST_ID = "request_id";
        public static final String START_TIME = "start_time";
    }

    public static final class ApiType {
        public static final String UNDEFINED = "UNDEFINED";
    }

    public enum RedisSerializer {
        DEFAULT, JSON
    }
}
