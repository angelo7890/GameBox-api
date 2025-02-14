package com.anjox.Gamebox_api.exeption;

import org.springframework.http.HttpStatus;

public class MessageErrorExeption extends RuntimeException {

    private final HttpStatus status;

    public MessageErrorExeption(String message , HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
