package com.wildtigerrr.StoryOfCamelot.bin.base.service;

import lombok.Getter;

public class ValidationResult {

    @Getter
    private String failMessage;
    private boolean isSuccess;

    public ValidationResult() {
        isSuccess = true;
    }

    public static ValidationResult get() {
        return new ValidationResult();
    }

    public void setFailMessage(String message) {
        isSuccess = false;
        failMessage = message;
    }

    public boolean success() {
        return isSuccess;
    }

    public boolean fail() {
        return !success();
    }

}
