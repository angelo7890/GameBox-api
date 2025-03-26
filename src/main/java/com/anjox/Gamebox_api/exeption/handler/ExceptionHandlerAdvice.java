package com.anjox.Gamebox_api.exeption.handler;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.exeption.PasswordErrorExeption;
import com.anjox.Gamebox_api.exeption.error.ResponseError;
import com.anjox.Gamebox_api.exeption.error.ResponsePasswordError;
import com.anjox.Gamebox_api.exeption.error.ResponseUnauthorizedError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(MessageErrorExeption.class)
    ResponseEntity<ResponseError> handleResponseMessageError(MessageErrorExeption exception) {
        ResponseError error = new ResponseError(exception.getMessage(), exception.getStatus());
        return new ResponseEntity<>(error, exception.getStatus());
    }

    @ExceptionHandler(PasswordErrorExeption.class)
    ResponseEntity<ResponsePasswordError> handleResponsePasswordError(PasswordErrorExeption exception) {
        ResponsePasswordError error = new ResponsePasswordError(exception.getErrors(), exception.getStatus());
        return new ResponseEntity<>(error, exception.getStatus());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ResponseError> handleException(Exception exception) {
        ResponseError error = new ResponseError("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}