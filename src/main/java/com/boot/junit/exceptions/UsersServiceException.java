package com.boot.junit.exceptions;

public class UsersServiceException extends RuntimeException {

    public UsersServiceException(String message) {
        super(message);
    }
}
