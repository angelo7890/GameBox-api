package com.anjox.Gamebox_api.exeption.error;

import org.springframework.http.HttpStatus;

public class ResponseUnauthorizedError {

    private String path;

    private String error;

    private String message;

    private HttpStatus status;

    public ResponseUnauthorizedError(String path, String error, String message, HttpStatus status) {
        this.path = path;
        this.error = error;
        this.message = message;
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
