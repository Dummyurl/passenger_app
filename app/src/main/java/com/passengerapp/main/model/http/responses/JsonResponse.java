package com.passengerapp.main.model.http.responses;

public class JsonResponse<T> {

    public T Content;
    public Boolean IsSuccess = false;
    public String Message;
    public Integer ResponseCode;
    public boolean IsAirport;
//    For SendCancelDriverApi
    public float ChargeAmount;
    public boolean IsDeposit;

    public JsonResponse(){

    }
}
