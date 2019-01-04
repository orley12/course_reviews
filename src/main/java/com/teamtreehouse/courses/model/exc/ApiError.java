package com.teamtreehouse.courses.model.exc;

public class ApiError extends RuntimeException{

    private int status;

    public ApiError(int status, String msg){
        super(msg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
