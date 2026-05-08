package com.workflow.infrastructure.ibm;

public class IbmBpmClientException extends RuntimeException {

    public IbmBpmClientException(String message) {
        super(message);
    }

    public IbmBpmClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
