
package com.boot.order.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFounds(
            EntityNotFoundException entityNotFoundException) {
        return createResponseEntity(HttpStatus.NOT_FOUND, entityNotFoundException);
    }

    @ExceptionHandler(InvalidInputDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> invalidInputData(
            InvalidInputDataException invalidInputDataException) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                invalidInputDataException);
    }

    @ExceptionHandler(DuplicateEntryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> duplicateEntry(
            DuplicateEntryException duplicateEntryException) {
        return createResponseEntity(HttpStatus.CONFLICT,
                duplicateEntryException);
    }

    @ExceptionHandler(UnableToModifyDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> unableToModifyEntry(
            UnableToModifyDataException unableToModifyDataException) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                unableToModifyDataException);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> constraintViolationException(
            ConstraintViolationException constraintViolationException) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                constraintViolationException);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> missingRequestHeaderException(
            MissingRequestHeaderException missingRequestHeaderException) {
        return createResponseEntity(HttpStatus.BAD_REQUEST,
                missingRequestHeaderException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        ErrorResponse error = new ErrorResponse();
        exception.getBindingResult().getFieldErrors().stream().forEach(item -> {
            error.messages.add(new ErrorMessage(item.getField(), item.getDefaultMessage()));
        });
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> exception(
            Exception exception) {
        log.error(exception);
        return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }

    private ResponseEntity<ApiError> createResponseEntity(HttpStatus httpStatus,
                                                          Exception e) {
        return new ResponseEntity<>(new ApiError(httpStatus, e.getMessage()),
                httpStatus);
    }


    class ErrorResponse {
        public List<ErrorMessage> messages = new ArrayList<>();
    }

    class ErrorMessage {
        public String fieldKey;
        public String message;

        public ErrorMessage(String fieldKey, String message) {
            this.fieldKey = fieldKey;
            this.message = message;
        }
    }

}
