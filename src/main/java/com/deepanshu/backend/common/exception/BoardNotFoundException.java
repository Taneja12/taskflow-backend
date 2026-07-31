package com.deepanshu.backend.common.exception;

public class BoardNotFoundException extends RuntimeException{
    public BoardNotFoundException(String message) {
        super(message);
    }
}
