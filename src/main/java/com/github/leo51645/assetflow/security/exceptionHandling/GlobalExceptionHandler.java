package com.github.leo51645.assetflow.security.exceptionHandling;

import com.github.leo51645.assetflow.investment_asset.exception.InvestAssetNotFoundException;
import com.github.leo51645.assetflow.marketdata.exception.*;
import com.github.leo51645.assetflow.security.exceptionHandling.exception.InvalidRefreshTokenException;
import com.github.leo51645.assetflow.security.exceptionHandling.exception.MissingRefreshTokenException;
import com.github.leo51645.assetflow.user.exception.EmailAlreadyExistsException;
import com.github.leo51645.assetflow.user.exception.InvalidPasswordException;
import com.github.leo51645.assetflow.user.exception.UserNotFoundException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Email already registered: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.CONFLICT, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | User not found: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(
            InvalidRefreshTokenException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Invalid refresh token: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(MissingRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleMissingRefreshToken(
            MissingRefreshTokenException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Missing refresh token: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(
            UsernameNotFoundException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Username not found: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed");

        log.warn("ErrorId: {} | Validation error: {}", errorId, message);

        return buildResponse(HttpStatus.BAD_REQUEST, message, request, errorId);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Constraint violation");

        log.warn("ErrorId: {} | Constraint violation: {}", errorId, message);

        return buildResponse(HttpStatus.BAD_REQUEST, message, request, errorId);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Invalid JSON: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST,
                "Invalid JSON format: " + e.getMostSpecificCause().getMessage(), request, errorId);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Data integrity violation: {}",
                errorId, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());

        return buildResponse(HttpStatus.CONFLICT,
                "A data conflict occurred. Please check your input.", request, errorId);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Invalid credentials attempt", errorId);

        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", request, errorId);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(
            InvalidPasswordException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Invalid password attempt: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleWrongSignature(
            SignatureException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Wrong token signature: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Access denied: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.FORBIDDEN,"You do not have permission to perform this action.", request, errorId);
    }

    @ExceptionHandler(InvestAssetNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAssetNotFound(
            InvestAssetNotFoundException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Asset not found: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooAssetNameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleYahooAssetNameNotFound(
            YahooAssetNameNotFoundException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Asset name not found: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooIsinNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleYahooIsinNotFound(
            YahooIsinNotFoundException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Isin not found: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooSymbolNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleYahooSymbolNotFound(
            YahooSymbolNotFoundException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Symbol not found: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooConnectionException.class)
    public ResponseEntity<ErrorResponse> handleYahooConnectionException(
            YahooConnectionException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo connection exception: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleYahooRateLimitException(
            YahooRateLimitException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo rate limit exception: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooServiceException.class)
    public ResponseEntity<ErrorResponse> handleYahooServiceException(
            YahooServiceException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo service exception: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.BAD_GATEWAY, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooSymbolMismatchException.class)
    public ResponseEntity<ErrorResponse> handleYahooSymbolMismatch(
            YahooSymbolMismatchException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo symbol mismatch: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.BAD_GATEWAY, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooInvalidResponseException.class)
    public ResponseEntity<ErrorResponse> handleYahooInvalidResponse(
            YahooInvalidResponseException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo invalid response: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.BAD_GATEWAY, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooMarketDataUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleYahooMarketDataUnavailable(
            YahooMarketDataUnavailableException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo market data unavailable: {}", errorId, e.getMessage());

        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), request, errorId);
    }

    @ExceptionHandler(YahooApiException.class)
    public ResponseEntity<ErrorResponse> handleYahooApiException(
            YahooApiException e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.warn("ErrorId: {} | Yahoo api exception: {}", errorId, e.getMessage());

        return buildResponse(e.getStatus(), e.getMessage(), request, errorId);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception e, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("ErrorId: {} | Unhandled exception: {}", errorId, e.getMessage(), e);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support. Error ID: " + errorId,
                request, errorId);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status, String message, HttpServletRequest request, String errorId) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .errorId(errorId)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
