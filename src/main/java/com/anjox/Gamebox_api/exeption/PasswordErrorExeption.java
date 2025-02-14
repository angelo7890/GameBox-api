package com.anjox.Gamebox_api.exeption;

import org.springframework.http.HttpStatus;

import java.util.List;

public class PasswordErrorExeption extends RuntimeException {

    private final List<String> errors;
    private final HttpStatus status;

    public PasswordErrorExeption(String message , List<String> errors , HttpStatus status) {
        super(message);
        this.errors = errors;
        this.status = status;
    }
    public HttpStatus getStatus() {
      return status;
    }

    public List<String> getErrors() {
      return errors;
    }

}
