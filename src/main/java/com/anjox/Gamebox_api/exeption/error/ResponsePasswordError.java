package com.anjox.Gamebox_api.exeption.error;
import org.springframework.http.HttpStatus;
import java.util.List;

public class ResponsePasswordError {

    private List<String> errors;
    private HttpStatus status;

    public ResponsePasswordError( List<String> errors , HttpStatus status ) {
        this.errors = errors;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
