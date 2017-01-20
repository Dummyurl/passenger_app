package com.passengerapp.main.model.http.responses;

public class HereApprovedJsonResponse<T> {

    public T content;
    public Boolean isSuccess = false;
    public String message;
    public Integer responseCode;
}
