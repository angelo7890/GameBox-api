package com.anjox.Gamebox_api.exeption.error;

import org.springframework.http.HttpStatus;

public class ResponseAccessDeniedHandlerError {

    private String error;

    private HttpStatus httpStatus;


    public ResponseAccessDeniedHandlerError(String error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
