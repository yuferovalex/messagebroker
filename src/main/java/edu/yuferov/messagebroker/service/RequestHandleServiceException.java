package edu.yuferov.messagebroker.service;

public class RequestHandleServiceException extends RuntimeException {
    public RequestHandleServiceException() {
    }

    public RequestHandleServiceException(String message) {
        super(message);
    }

    public RequestHandleServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestHandleServiceException(Throwable cause) {
        super(cause);
    }
}
