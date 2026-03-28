package org.amts.adapters.http.advice;

import org.amts.application.exceptions.PersistenceException;
import org.amts.application.exceptions.PermissionException;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.exceptions.user.role.IllegalRoleAssignmentException;
import org.amts.application.exceptions.user.role.UnauthorizedRoleAssignmentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String errorMsg = String.format(
                "Invalid format for parameter '%s'. Expected type '%s'.",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", errorMsg));
    }

    // 404
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    // 403
    @ExceptionHandler(UnauthorizedRoleAssignmentException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedRoleAssignment(
            UnauthorizedRoleAssignmentException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    // 400
    @ExceptionHandler(IllegalRoleAssignmentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalRoleAssignment(IllegalRoleAssignmentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // 403 (generic permission)
    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<Map<String, String>> handlePermission(PermissionException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }

    // 500 (infrastructure)
    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<Map<String, String>> handlePersistence(PersistenceException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", ex.getMessage()));
    }

    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected processing error occurred."));
    }
}