package com.pht.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.pht.common.constants.CoreConstants;
import com.pht.common.model.ApiDataResponse;
import com.pht.utils.DateTimeUtils;
import com.pht.utils.HttpUtils;
import com.pht.utils.LogUtils;

import java.time.LocalDateTime;

@Order(1)
@ControllerAdvice
@RequiredArgsConstructor
@ConditionalOnProperty(value = "backend.add-info-to-response-body", havingValue = "true", matchIfMissing = true)
public class AddInfoResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof ApiDataResponse<?> apiDataResponse && request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();
            Object requestId = HttpUtils.getRequestAttribute(httpServletRequest, CoreConstants.RequestAttribute.REQUEST_ID);
            Object startTimeObj = HttpUtils.getRequestAttribute(httpServletRequest, CoreConstants.RequestAttribute.START_TIME);
            Long startTime = startTimeObj != null ? Long.valueOf(String.valueOf(startTimeObj)) : null;
            LocalDateTime endTime = LocalDateTime.now();
            long endTimeMilli = DateTimeUtils.localDateTimeToMilli(endTime);

            apiDataResponse.setRequestId(requestId != null ? String.valueOf(requestId) : null);
            apiDataResponse.setStartTime(startTime);
            apiDataResponse.setExecutionTime(startTime != null ? LogUtils.getExecutionTime(startTime, endTimeMilli) : null);
            apiDataResponse.setTimestamp(endTime);
            apiDataResponse.setEndTime(endTimeMilli);
        }

        return body;
    }
}
