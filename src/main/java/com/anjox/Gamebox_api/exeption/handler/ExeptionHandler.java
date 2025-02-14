package com.anjox.Gamebox_api.exeption.handler;
import com.anjox.Gamebox_api.exeption.MessageErrorExeption;
import com.anjox.Gamebox_api.exeption.PasswordErrorExeption;
import com.anjox.Gamebox_api.exeption.error.ResponseError;
import com.anjox.Gamebox_api.exeption.error.ResponsePasswordError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExeptionHandler {
    @ExceptionHandler(MessageErrorExeption.class)
    public ResponseEntity<ResponseError> responseMessageError(MessageErrorExeption exeption) {
        ResponseError error = new ResponseError(exeption.getMessage(), exeption.getStatus());
        return new ResponseEntity<>(error, exeption.getStatus());
    }

    @ExceptionHandler(PasswordErrorExeption.class)
    public ResponseEntity<ResponsePasswordError> responsePasswordError(PasswordErrorExeption exeption) {
        ResponsePasswordError error = new ResponsePasswordError(exeption.getErrors(), exeption.getStatus());
        return new ResponseEntity<>(error, exeption.getStatus());
    }
}
