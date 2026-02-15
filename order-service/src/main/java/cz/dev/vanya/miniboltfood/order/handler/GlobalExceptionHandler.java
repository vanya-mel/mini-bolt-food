package cz.dev.vanya.miniboltfood.order.handler;

import cz.dev.vanya.miniboltfood.commonlibs.payload.response.ApiErrorResponse;
import cz.dev.vanya.miniboltfood.commonlibs.utils.ApiErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(final MethodArgumentNotValidException ex,
                                                             final HttpServletRequest request) {
        final Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), ApiErrorUtils.INVALID_ERROR_MESSAGE),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        final ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ApiErrorUtils.VALIDATION_FAILED_MESSAGE,
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    // Handle @Validated on path variables / request params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(final ConstraintViolationException ex,
                                                                      final HttpServletRequest request) {

        final Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> Objects.requireNonNullElse(v.getMessage(), ApiErrorUtils.INVALID_ERROR_MESSAGE),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        final ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(final ResponseStatusException ex,
                                                                 final HttpServletRequest request) {
        final HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        final ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                Objects.requireNonNullElse(ex.getReason(), status.getReasonPhrase()),
                request.getRequestURI(),
                Collections.emptyMap()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(final Exception ex,
                                                             final HttpServletRequest request) {
        log.error("Unhandled exception on path={}", request.getRequestURI(), ex);

        final ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ApiErrorUtils.UNEXPECTED_ERROR_MESSAGE,
                request.getRequestURI(),
                Collections.emptyMap()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
