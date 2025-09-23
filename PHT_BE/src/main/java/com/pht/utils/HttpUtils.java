package com.pht.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pht.common.model.RestClientResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtils {

    public static String getHeaderValue(HttpHeaders httpHeaders, String key) {
        if (!ValidationUtils.isNullOrEmpty(httpHeaders)) {
            List<String> attrs = httpHeaders.get(key);
            return !ValidationUtils.isNullOrEmpty(attrs) ? attrs.get(0) : "";
        }

        return "";
    }

    public static Object getRequestAttribute(HttpServletRequest request, String attribute) {
        if (request != null && attribute != null) {
            return request.getAttribute(attribute);
        }

        return null;
    }

    /**
     * Get the request attribute from RequestContextHolder.
     */
    public static Object getRequestAttribute(String attribute) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? requestAttributes.getAttribute(attribute, RequestAttributes.SCOPE_REQUEST) : null;
    }

    public static HttpHeaders getHttpHeadersFromJson(String json) {
        if (ValidationUtils.isNullOrEmpty(json)) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        Map<String, Object> headerMap = ObjectUtils.toObject(json, new TypeReference<>() {
        });
        if (!ValidationUtils.isNullOrEmpty(headerMap)) {
            headerMap.forEach((key, val) -> {
                if (val instanceof List) {
                    headers.addAll(key, (List) val);
                } else if (val != null) {
                    headers.add(key, val.toString());
                } else {
                    headers.add(key, null);
                }
            });
        }

        return headers;
    }

    public static HttpHeaders getHttpHeadersFromMap(Map<String, String> headerMap) {
        HttpHeaders headers = new HttpHeaders();
        if (headerMap != null) {
            headerMap.forEach(headers::add);
        }

        return headers;
    }

    public static String getResponseBody(ClientHttpResponse response) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(response.getBody(), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return bufferedReader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public String detectContentType(HttpServletRequest request, String path) {
        if (request != null) {
            request.getServletContext().getMimeType(path);
        }

        return null;
    }

    public static boolean isBodyLogable(HttpHeaders headers) {
        if (headers != null) {
            ContentDisposition contentDisposition = headers.getContentDisposition();
            return !contentDisposition.isAttachment()
                    && !contentDisposition.isInline()
                    && !contentDisposition.isFormData()
                    && !isMultiPartFormData(headers);
        }

        return false;
    }

    public static boolean isMultiPartFormData(HttpHeaders headers) {
        return headers != null
                && headers.getContentType() != null
                && headers.getContentType().getType().equals(MediaType.MULTIPART_FORM_DATA.getType())
                && headers.getContentType().getSubtype().equals(MediaType.MULTIPART_FORM_DATA.getSubtype());
    }

    /**
     * Get the current HttpServletRequest using RequestContextHolder.
     */
    public static HttpServletRequest getCurrentHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? ((ServletRequestAttributes) requestAttributes).getRequest() : null;
    }

    /**
     * Extract the http status and response when the rest call has exception
     */
    public static RestClientResponse getRestClientResponseFromException(Exception ex) {
        if (ex instanceof RestClientResponseException restEx) {
            return RestClientResponse.builder()
                    .rawStatusCode(restEx.getRawStatusCode())
                    .statusText(restEx.getStatusText())
                    .responseBody(restEx.getResponseBodyAsByteArray())
                    .responseBodyAsString(restEx.getResponseBodyAsString())
                    .build();
        }

        if (ex != null) {
            return RestClientResponse.builder()
                    .rawStatusCode(RestClientResponse.UNKNOWN_STATUS_CODE)
                    .statusText(RestClientResponse.UNKNOWN_STATUS_TEXT)
                    .responseBodyAsString(ex.getMessage())
                    .build();
        }

        return null;
    }
}