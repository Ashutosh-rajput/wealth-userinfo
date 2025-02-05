package com.WealthManager.UserInfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ReportAlreadyExitsorConflict extends RuntimeException{
    public ReportAlreadyExitsorConflict(String message) {
        super(message);
    }
}

