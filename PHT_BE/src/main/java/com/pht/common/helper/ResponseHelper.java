package com.pht.common.helper;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.pht.common.OrderBy;
import com.pht.common.model.response.ApiDataResponse;
import com.pht.constants.ResponseEnum;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseHelper {

    public static ResponseEntity<Object> ok() {
        return ok(null);
    }

    public static ResponseEntity<Object> okWithMessage(String message) {
        return ok(null, message);
    }

    public static <T> ResponseEntity<Object> ok(@Nullable T body) {
        return ok(body, ResponseEnum.SUCCESS.message);
    }

    public static <T> ResponseEntity<Object> ok(@Nullable T body, String message) {
        return ok(body, HttpStatus.OK, message);
    }

    public static <T> ResponseEntity<Object> ok(T body, HttpStatus status, String message) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiDataResponse.builder().status(status.value()).message(message).data(body).build());
    }

    public static ResponseEntity<Object> noContent() {
        return ResponseEntity.noContent().build();
    }

    public static ResponseEntity<Object> invalid(String message) {
        return invalid(null, message);
    }

    public static <T> ResponseEntity<Object> invalid(@Nullable T body, String message) {
        return ok(body, HttpStatus.BAD_REQUEST, message);
    }

    public static ResponseEntity<Object> fail() {
        return fail(null);
    }

    public static <T> ResponseEntity<Object> fail(@Nullable T body) {
        return fail(body, ResponseEnum.FAIL.message);
    }

    public static ResponseEntity<Object> failWithException(@Nullable Exception ex) {
        return fail(null, ex != null ? ex.getMessage() : null);
    }

    public static <T> ResponseEntity<Object> fail(@Nullable T body, String message) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return createResponseEntity(status, ApiDataResponse.builder().status(status.value()).message(message).data(body).build());
    }

    public static ResponseEntity<Object> notFound() {
        return notFound(null);
    }

    public static <T> ResponseEntity<Object> notFound(@Nullable T body) {
        return notFound(body, ResponseEnum.NOT_FOUND.message);
    }

    public static <T> ResponseEntity<Object> notFound(@Nullable T body, String message) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return createResponseEntity(status, ApiDataResponse.builder().status(status.value()).message(message).data(body).build());
    }

    public static ResponseEntity<Object> error(@Nullable Exception ex) {
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiDataResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(ex != null ? ex.getMessage() : null)
                        .data(null)
                        .build()
                );
    }

    public static <T> ResponseEntity<Object> createResponseEntity(HttpStatus status, T body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    public static OrderBy.ApiErrorResponse<Object> getApiErrorResponse(HttpStatus status, Exception ex) {
        return getApiErrorResponse(status, ex, getErrorMessage());
    }

    public static OrderBy.ApiErrorResponse<Object> getApiErrorResponse(HttpStatus status, Exception ex, String error) {
        return getApiErrorResponse(status, ex, error, null);
    }

    public static OrderBy.ApiErrorResponse<Object> getApiErrorResponse(HttpStatus status, Exception ex, List<String> errors) {
        return getApiErrorResponse(status, ex, getErrorMessage(), errors);
    }

    public static OrderBy.ApiErrorResponse<Object> getApiErrorResponse(HttpStatus status, Exception ex, String error, List<String> errors) {
        return OrderBy.ApiErrorResponse.apiErrorResponseBuilder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(error)
                .error(ex.getLocalizedMessage())
                .errors(errors)
                .build();
    }

    public static OrderBy.ApiErrorResponse<Object> getApiErrorResponse(HttpStatus status, String errorCode, String message, List<String> errors) {
        return OrderBy.ApiErrorResponse.apiErrorResponseBuilder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .error(errorCode)
                .errors(errors)
                .build();
    }

    public static String getErrorMessage(@NonNull Exception ex) {
        return getErrorMessage() + (ex.getCause() != null ? ": " + ex.getCause().getMessage() : "");
    }

    public static String getErrorMessage() {
        return "Error occurred";
    }

    public static ResponseEntity<?> errorTdt(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value()); // HTTP Status Code (e.g., 404, 500)
        errorResponse.put("error", status.getReasonPhrase()); // Reason (e.g., Not Found)
        errorResponse.put("message", message); // Custom error message
        errorResponse.put("timestamp", LocalDateTime.now()); // Timestamp for debugging
        
        return new ResponseEntity<>(errorResponse, status);
    }

}
