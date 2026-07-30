package com.deepanshu.backend.common.exception;


public class WorkSpaceNotFoundException extends RuntimeException{
    public WorkSpaceNotFoundException(String message) {
        super(message);
    }
}
