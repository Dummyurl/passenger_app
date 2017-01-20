package com.passengerapp.main.network.model.response;

public class JsonServerResponse<T> {
    public T getContent() throws Exception {
        if(Content == null) {
            throw new Exception();
        }
        return Content;
    }

    public T Content;
    public Boolean IsSuccess = false;
    public String Message;
    public Integer ResponseCode;
    public boolean IsAirport;
    public float ChargeAmount;
    public boolean IsDeposit;
}