package com.elgar.walletsystem.exception;

import com.elgar.walletsystem.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex, HttpStatus status, HttpServletRequest request, ErrorCode code) {

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                code
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(CustomExceptionHandler.WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(
            CustomExceptionHandler.WalletNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request, ErrorCode.WALLET_NOT_FOUND);
    }

    @ExceptionHandler(CustomExceptionHandler.InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(
            CustomExceptionHandler.InsufficientBalanceException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request, ErrorCode.INSUFFICIENT_FUNDS);
    }

    @ExceptionHandler(CustomExceptionHandler.DuplicateTransactionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTransaction(
            CustomExceptionHandler.DuplicateTransactionException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request, ErrorCode.DUPLICATE_TRANSACTION);
    }

    @ExceptionHandler(CustomExceptionHandler.ReconciliationFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReconciliationFileNotFound(
            CustomExceptionHandler.ReconciliationFileNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request, ErrorCode.RECONCILIATION_FILE_NOT_FOUND);
    }

    @ExceptionHandler(CustomExceptionHandler.BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            CustomExceptionHandler.BusinessRuleException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNPROCESSABLE_ENTITY, request, ErrorCode.BUSINESS_RULE_VIOLATION);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request, ErrorCode.INTERNAL_ERROR);
    }
}